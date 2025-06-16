package com.superlawva.domain.ocr.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.superlawva.domain.ocr.dto.ocr.OcrJobResponseDTO;
import com.superlawva.domain.ocr.dto.ocr.OcrRequestDTO;
import com.superlawva.domain.ocr.dto.ocr.OcrResultDTO;
import com.superlawva.domain.ocr.dto.ocr.OcrResultUpdateDTO;
import com.superlawva.domain.ocr.dto.ocr.OcrStatusDTO;
import com.superlawva.domain.ocr.entity.ContractDetails;
import com.superlawva.domain.ocr.entity.Document;
import com.superlawva.domain.ocr.entity.OcrJob;
import com.superlawva.domain.ocr.entity.PropertyInfo;
import com.superlawva.domain.ocr.entity.RentalContract;
import com.superlawva.domain.ocr.exception.OcrProcessingException;
import com.superlawva.domain.ocr.repository.DocumentRepository;
import com.superlawva.domain.ocr.repository.OcrJobRepository;
import com.superlawva.domain.ocr.repository.RentalContractRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OcrService {

    private final OcrJobRepository ocrJobRepository;
    private final RentalContractRepository rentalContractRepository;
    private final DocumentRepository documentRepository;
    private final Optional<GcpDocumentAiService> gcpDocumentAiService;

    private static final double confidenceThreshold = 0.7;

    public OcrJobResponseDTO createOcrJob(Long documentId, OcrRequestDTO request) {
        log.info("Creating OCR job for document: {}", documentId);

        // 🟢 테스트용: 문서가 없어도 OCR 진행 가능하도록 수정
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isEmpty()) {
            log.warn("Document {} not found, creating mock OCR job for testing", documentId);
        }

        // 기존 OCR 작업 확인
        Optional<OcrJob> existingJob = ocrJobRepository.findTopByDocumentIdOrderByCreatedAtDesc(documentId);
        if (existingJob.isPresent() && existingJob.get().getStatus() == OcrJob.OcrStatus.PROCESSING) {
            return OcrJobResponseDTO.builder()
                    .ocrJobId(existingJob.get().getId().toString())
                    .documentId(documentId)
                    .status("PROCESSING")
                    .estimatedCompletionTime("2-3 minutes")
                    .build();
        }

        // 새 OCR 작업 생성
        OcrJob ocrJob = OcrJob.builder()
                .documentId(documentId)
                .status(OcrJob.OcrStatus.PENDING)
                .build();

        ocrJob = ocrJobRepository.save(ocrJob);

        // 비동기 OCR 처리 시작 (실제 구현에서는 별도 처리)
        processOcrAsync(ocrJob.getId(), request);

        return OcrJobResponseDTO.builder()
                .ocrJobId(ocrJob.getId().toString())
                .documentId(documentId)
                .status("PENDING")
                .estimatedCompletionTime("2-3 minutes")
                .build();
    }

    public OcrStatusDTO getOcrStatus(Long documentId) {
        OcrJob ocrJob = ocrJobRepository.findTopByDocumentIdOrderByCreatedAtDesc(documentId)
                .orElseThrow(() -> new OcrProcessingException("OCR job not found for document: " + documentId));

        return OcrStatusDTO.builder()
                .documentId(documentId)
                .ocrStatus(ocrJob.getStatus().name())
                .ocrConfidence(ocrJob.getConfidence())
                .completedAt(ocrJob.getCompletedAt() != null ? ocrJob.getCompletedAt().toString() : null)
                .processingTimeMs(ocrJob.getProcessingTimeMs())
                .build();
    }

    public OcrResultDTO getOcrResult(Long documentId) {
        OcrJob ocrJob = ocrJobRepository.findTopByDocumentIdOrderByCreatedAtDesc(documentId)
                .orElseThrow(() -> new OcrProcessingException("OCR job not found for document: " + documentId));

        if (ocrJob.getStatus() != OcrJob.OcrStatus.COMPLETED) {
            throw new OcrProcessingException("OCR job is not completed yet");
        }

        // 파싱된 계약서 데이터 조회
        Optional<RentalContract> contract = rentalContractRepository.findByOcrJobId(ocrJob.getId());

        OcrResultDTO.ParsedContract parsedContract = null;
        if (contract.isPresent()) {
            parsedContract = buildParsedContractDTO(contract.get(), ocrJob.getRawText());
        }

        return OcrResultDTO.builder()
                .documentId(documentId)
                .ocrStatus(ocrJob.getStatus().name())
                .ocrConfidence(ocrJob.getConfidence())
                .completedAt(ocrJob.getCompletedAt().toString())
                .processingTimeMs(ocrJob.getProcessingTimeMs())
                .ocrResult(OcrResultDTO.OcrTextResult.builder()
                        .rawText(ocrJob.getRawText())
                        .confidence(ocrJob.getConfidence())
                        .build())
                .parsedContract(parsedContract)
                .build();
    }

    public void updateOcrResult(Long documentId, OcrResultUpdateDTO updateRequest) {
        OcrJob ocrJob = ocrJobRepository.findTopByDocumentIdOrderByCreatedAtDesc(documentId)
                .orElseThrow(() -> new OcrProcessingException("OCR job not found for document: " + documentId));

        Optional<RentalContract> contract = rentalContractRepository.findByOcrJobId(ocrJob.getId());
        if (contract.isPresent()) {
            updateRentalContractFromDTO(contract.get(), updateRequest.getParsedContract());
            rentalContractRepository.save(contract.get());
        }
    }

    private void processOcrAsync(Long ocrJobId, OcrRequestDTO request) {
        // ⚠️ 이 메서드는 파일 데이터 없이 호출되므로 사용하지 않습니다.
        // 실제 OCR 처리는 /api/upload/ocr 엔드포인트에서 수행됩니다.
        try {
            OcrJob ocrJob = ocrJobRepository.findById(ocrJobId).orElseThrow();
            ocrJob.setStatus(OcrJob.OcrStatus.FAILED);
            ocrJob.setErrorMessage("이 메서드는 사용되지 않습니다. /api/upload/ocr 엔드포인트를 사용하세요.");
            ocrJob.setProcessingTimeMs(0);
            ocrJobRepository.save(ocrJob);

            log.warn("⚠️ processOcrAsync가 호출되었지만 파일 데이터가 없어 처리할 수 없습니다. /api/upload/ocr 엔드포인트를 사용하세요.");

        } catch (Exception e) {
            log.error("OCR job status update failed for job: {}", ocrJobId, e);
        }
    }



    private void parseAndSaveContract(OcrJob ocrJob, String text) {
        // 계약 유형 판단
        RentalContract.ContractType contractType = text.contains("월세") ?
                RentalContract.ContractType.MONTHLY : RentalContract.ContractType.JEONSE;

        // 파싱 수행
        OcrResultDTO.PropertyInfo propertyInfoDto = parsePropertyInfo(text);
        OcrResultDTO.ContractDetails contractDetailsDto = parseContractDetails(text, contractType);
        OcrResultDTO.SpecialTerms specialTerms = parseSpecialTerms(text);
        OcrResultDTO.ContractArticles contractArticles = parseContractArticles(text);

        // 엔티티 필드 생성
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setAddress(propertyInfoDto.getPropertyAddress());
        propertyInfo.setDetailedAddress(propertyInfoDto.getDetailedAddress());
        propertyInfo.setAreaSqm(propertyInfoDto.getAreaSqm());
        propertyInfo.setAreaPyeong(propertyInfoDto.getAreaPyeong());
        propertyInfo.setFloor(propertyInfoDto.getFloor());
        propertyInfo.setBuildingType(propertyInfoDto.getBuildingType());
        propertyInfo.setBuildingStructure(propertyInfoDto.getBuildingStructure());

        ContractDetails contractDetails = new ContractDetails();
        contractDetails.setContractDate(contractDetailsDto.getContractDate());
        contractDetails.setLeaseStartDate(contractDetailsDto.getLeaseStartDate());
        contractDetails.setLeaseEndDate(contractDetailsDto.getLeaseEndDate());
        contractDetails.setDepositAmount(contractDetailsDto.getDepositAmount());
        contractDetails.setMonthlyRent(contractDetailsDto.getMonthlyRent());
        contractDetails.setMaintenanceFee(contractDetailsDto.getMaintenanceFee());
        contractDetails.setRentPaymentDate(contractDetailsDto.getPaymentDate());

        // RentalContract 엔티티 생성 및 저장
        RentalContract contract = RentalContract.builder()
                .ocrJob(ocrJob)
                .contractType(contractType)
                .propertyInfo(propertyInfo)
                .contractDetails(contractDetails)
                .build();

        rentalContractRepository.save(contract);
    }

    private OcrResultDTO.ParsedContract buildParsedContractDTO(RentalContract contract, String rawText) {
        OcrResultDTO.PropertyInfo propertyInfo = parsePropertyInfo(rawText);
        OcrResultDTO.ContractDetails contractDetails = parseContractDetails(rawText, contract.getContractType());
        OcrResultDTO.SpecialTerms specialTerms = parseSpecialTerms(rawText);
        OcrResultDTO.ContractArticles contractArticles = parseContractArticles(rawText);

        OcrResultDTO.ParsedContract parsedContract = OcrResultDTO.ParsedContract.builder()
                .contractType(contract.getContractType().name())
                .propertyInfo(propertyInfo)
                .contractDetails(contractDetails)
                .specialTerms(specialTerms)
                .contractArticles(contractArticles)
                .build();

        // 계약 요약 생성
        parsedContract.setContractSummary(generateContractSummary(parsedContract));

        return parsedContract;
    }

    private void updateRentalContractFromDTO(RentalContract contract, OcrResultDTO.ParsedContract parsedContract) {
        if (parsedContract.getPropertyInfo() != null) {
            PropertyInfo propertyInfo = contract.getPropertyInfo();
            if (propertyInfo == null) {
                propertyInfo = new PropertyInfo();
                contract.setPropertyInfo(propertyInfo);
            }
            propertyInfo.setAddress(parsedContract.getPropertyInfo().getPropertyAddress());
            propertyInfo.setDetailedAddress(parsedContract.getPropertyInfo().getDetailedAddress());
            propertyInfo.setAreaSqm(parsedContract.getPropertyInfo().getAreaSqm());
            propertyInfo.setAreaPyeong(parsedContract.getPropertyInfo().getAreaPyeong());
            propertyInfo.setFloor(parsedContract.getPropertyInfo().getFloor());
            propertyInfo.setBuildingType(parsedContract.getPropertyInfo().getBuildingType());
            propertyInfo.setBuildingStructure(parsedContract.getPropertyInfo().getBuildingStructure());
        }

        if (parsedContract.getContractDetails() != null) {
            ContractDetails contractDetails = contract.getContractDetails();
            if (contractDetails == null) {
                contractDetails = new ContractDetails();
                contract.setContractDetails(contractDetails);
            }
            OcrResultDTO.ContractDetails details = parsedContract.getContractDetails();
            contractDetails.setDepositAmount(details.getDepositAmount());
            contractDetails.setMonthlyRent(details.getMonthlyRent());
            contractDetails.setLeaseStartDate(details.getLeaseStartDate());
            contractDetails.setLeaseEndDate(details.getLeaseEndDate());
            contractDetails.setContractDate(details.getContractDate());
            contractDetails.setMaintenanceFee(details.getMaintenanceFee());
            contractDetails.setRentPaymentDate(details.getPaymentDate());
        }
    }

    private OcrResultDTO.PropertyInfo parsePropertyInfo(String text) {
        if (text == null || text.isEmpty()) {
            return OcrResultDTO.PropertyInfo.builder().build();
        }

        var builder = OcrResultDTO.PropertyInfo.builder();

        // 주소 추출
        Pattern addressPattern = Pattern.compile("(?:소재지|주\\s*소|위치)[:\\s]*([가-힣\\d\\s\\-,]+(?:구|시|군|동|로|길)[^\\n]*)", Pattern.CASE_INSENSITIVE);
        Matcher addressMatcher = addressPattern.matcher(text);
        if (addressMatcher.find()) {
            String fullAddress = addressMatcher.group(1).trim();

            // 상세주소 분리
            Pattern detailPattern = Pattern.compile("((?:아파트|빌라|오피스텔|주택).*?(?:\\d+동\\s*\\d+호|\\d+호))");
            Matcher detailMatcher = detailPattern.matcher(fullAddress);
            if (detailMatcher.find()) {
                builder.detailedAddress(detailMatcher.group(1));
                builder.propertyAddress(fullAddress.replace(detailMatcher.group(1), "").trim());
            } else {
                builder.propertyAddress(fullAddress);
            }
        }

        // 면적 추출 (제곱미터)
        Pattern areaPattern = Pattern.compile("(?:전용|공급|면적)[^\\d]*(\\d+\\.?\\d*)\\s*(?:㎡|m2)", Pattern.CASE_INSENSITIVE);
        Matcher areaMatcher = areaPattern.matcher(text);
        if (areaMatcher.find()) {
            double sqm = Double.parseDouble(areaMatcher.group(1));
            builder.areaSqm(sqm);
            builder.areaPyeong(Math.round(sqm / 3.3 * 10.0) / 10.0); // 평수 계산
        }

        // 층수
        Pattern floorPattern = Pattern.compile("(\\d+)\\s*층", Pattern.CASE_INSENSITIVE);
        Matcher floorMatcher = floorPattern.matcher(text);
        if (floorMatcher.find()) {
            builder.floor(floorMatcher.group(1) + "층");
        }

        // 건물 유형
        String[] buildingTypes = {"아파트", "오피스텔", "빌라", "연립주택", "다세대주택", "단독주택", "상가"};
        for (String type : buildingTypes) {
            if (text.contains(type)) {
                builder.buildingType(type);
                break;
            }
        }

        // 건물 구조
        String[] structures = {"철근콘크리트", "벽돌조", "목조", "철골조"};
        for (String structure : structures) {
            if (text.contains(structure)) {
                builder.buildingStructure(structure);
                break;
            }
        }

        // 지목
        String[] landTypes = {"대지", "전", "답", "임야", "잡종지"};
        for (String landType : landTypes) {
            if (text.contains(landType)) {
                builder.landClassification(landType);
                break;
            }
        }

        // 소유권 구분
        if (text.contains("집합건물")) {
            builder.ownershipType("집합건물");
        } else if (text.contains("단독소유")) {
            builder.ownershipType("단독소유");
        }

        return builder.build();
    }

    private OcrResultDTO.ContractDetails parseContractDetails(String text, RentalContract.ContractType type) {
        if (text == null || text.isEmpty()) {
            return OcrResultDTO.ContractDetails.builder()
                    .contractType(type == RentalContract.ContractType.JEONSE ? "전세" : "월세")
                    .build();
        }

        var builder = OcrResultDTO.ContractDetails.builder();
        builder.contractType(type == RentalContract.ContractType.JEONSE ? "전세" : "월세");

        // 🟢 개선된 보증금 추출 (다양한 패턴 지원)
        Pattern[] depositPatterns = {
                Pattern.compile("보\\s*증\\s*금[:\\s]*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("₩([0-9,]+).*?보\\s*증\\s*금", Pattern.CASE_INSENSITIVE),
                Pattern.compile("보\\s*증\\s*금[:\\s]*금\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("금\\s*오\\s*백만원\\s*원정[:\\s]*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("₩([0-9,]+)", Pattern.CASE_INSENSITIVE) // 기본 패턴
        };

        boolean foundDeposit = false;
        for (Pattern pattern : depositPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String depositStr = matcher.group(1).replaceAll(",", "");
                try {
                    long deposit = Long.parseLong(depositStr);
                    // 만원 단위로 변환 (Integer로 캐스팅)
                    builder.depositAmount((int)(deposit / 10000));
                    foundDeposit = true;
                    break;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

        if (!foundDeposit) {
            builder.depositAmount(0);
        }

        // 월세 추출 (월세 계약인 경우)
        if (type == RentalContract.ContractType.MONTHLY) {
            Pattern rentPattern = Pattern.compile("월\\s*세[:\\s]*(?:금\\s*)?₩?\\s*([0-9,]+)\\s*만원", Pattern.CASE_INSENSITIVE);
            Matcher rentMatcher = rentPattern.matcher(text);
            if (rentMatcher.find()) {
                String rentStr = rentMatcher.group(1).replaceAll(",", "");
                int rent = Integer.parseInt(rentStr);
                // 이미 만원 단위이므로 그대로 사용
                builder.monthlyRent(rent);
            }

            // 월세 납부일
            Pattern paymentDatePattern = Pattern.compile("매월\\s*(\\d{1,2})일", Pattern.CASE_INSENSITIVE);
            Matcher paymentDateMatcher = paymentDatePattern.matcher(text);
            if (paymentDateMatcher.find()) {
                builder.paymentDate(Integer.parseInt(paymentDateMatcher.group(1)));
            }
        } else {
            builder.monthlyRent(0);
        }

        // 관리비
        Pattern maintenancePattern = Pattern.compile("관\\s*리\\s*비[:\\s]*(?:금\\s*)?₩?\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE);
        Matcher maintenanceMatcher = maintenancePattern.matcher(text);
        if (maintenanceMatcher.find()) {
            String maintenanceStr = maintenanceMatcher.group(1).replaceAll(",", "");
            int maintenance = Integer.parseInt(maintenanceStr);
            if (maintenance > 10000) {
                maintenance = maintenance / 10000;
            }
            builder.maintenanceFee(maintenance);
        }

        // 날짜 추출
        String datePattern = "(\\d{4})\\s*년\\s*(\\d{1,2})\\s*월\\s*(\\d{1,2})\\s*일";

        // 계약일
        Pattern contractDatePattern = Pattern.compile("계\\s*약\\s*일[:\\s]*" + datePattern, Pattern.CASE_INSENSITIVE);
        Matcher contractDateMatcher = contractDatePattern.matcher(text);
        if (contractDateMatcher.find()) {
            LocalDate date = LocalDate.of(
                    Integer.parseInt(contractDateMatcher.group(1)),
                    Integer.parseInt(contractDateMatcher.group(2)),
                    Integer.parseInt(contractDateMatcher.group(3))
            );
            builder.contractDate(date);
        }

        // 계약 기간
        Pattern periodPattern = Pattern.compile(datePattern + ".*?부터.*?" + datePattern + ".*?까지", Pattern.CASE_INSENSITIVE);
        Matcher periodMatcher = periodPattern.matcher(text);
        if (periodMatcher.find()) {
            LocalDate startDate = LocalDate.of(
                    Integer.parseInt(periodMatcher.group(1)),
                    Integer.parseInt(periodMatcher.group(2)),
                    Integer.parseInt(periodMatcher.group(3))
            );
            LocalDate endDate = LocalDate.of(
                    Integer.parseInt(periodMatcher.group(4)),
                    Integer.parseInt(periodMatcher.group(5)),
                    Integer.parseInt(periodMatcher.group(6))
            );

            builder.leaseStartDate(startDate);
            builder.leaseEndDate(endDate);

            // 계약 기간 계산 (개월)
            int months = (endDate.getYear() - startDate.getYear()) * 12 +
                    (endDate.getMonthValue() - startDate.getMonthValue());
            builder.leasePeriodMonths(months);
        }

        // 입주일
        Pattern moveInPattern = Pattern.compile("입\\s*주\\s*일[:\\s]*" + datePattern, Pattern.CASE_INSENSITIVE);
        Matcher moveInMatcher = moveInPattern.matcher(text);
        if (moveInMatcher.find()) {
            LocalDate date = LocalDate.of(
                    Integer.parseInt(moveInMatcher.group(1)),
                    Integer.parseInt(moveInMatcher.group(2)),
                    Integer.parseInt(moveInMatcher.group(3))
            );
            builder.moveInDate(date);
        }

        // 납부 방법
        Pattern paymentMethodPattern = Pattern.compile("(현금|계좌이체|자동이체)", Pattern.CASE_INSENSITIVE);
        Matcher paymentMethodMatcher = paymentMethodPattern.matcher(text);
        if (paymentMethodMatcher.find()) {
            builder.paymentMethod(paymentMethodMatcher.group(1));
        }

        return builder.build();
    }

    private OcrResultDTO.SpecialTerms parseSpecialTerms(String text) {
        if (text == null || text.isEmpty()) {
            return OcrResultDTO.SpecialTerms.builder()
                    .defaultTerms(new ArrayList<>())
                    .additionalTerms(new ArrayList<>())
                    .build();
        }

        List<OcrResultDTO.SpecialTerms.SpecialTerm> defaultTerms = new ArrayList<>();
        List<OcrResultDTO.SpecialTerms.SpecialTerm> additionalTerms = new ArrayList<>();

        // 특약사항 항목 분리
        String[] lines = text.split("\\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // 번호가 있는 항목 처리
            Pattern itemPattern = Pattern.compile("^\\d+\\.?\\s*(.+)$");
            Matcher itemMatcher = itemPattern.matcher(line);

            String content = itemMatcher.find() ? itemMatcher.group(1) : line;

            // 기본 특약사항 키워드
            if (content.contains("보증금반환") || content.contains("수선의무") ||
                    content.contains("전대제한") || content.contains("중도해지") ||
                    content.contains("갱신우선권")) {

                defaultTerms.add(OcrResultDTO.SpecialTerms.SpecialTerm.builder()
                        .termId("default_" + defaultTerms.size())
                        .content(content)
                        .build());
            } else if (content.length() > 5) { // 너무 짧은 내용 제외
                additionalTerms.add(OcrResultDTO.SpecialTerms.SpecialTerm.builder()
                        .termId("additional_" + additionalTerms.size())
                        .content(content)
                        .build());
            }
        }

        return OcrResultDTO.SpecialTerms.builder()
                .defaultTerms(defaultTerms)
                .additionalTerms(additionalTerms)
                .build();
    }

    private OcrResultDTO.ContractArticles parseContractArticles(String text) {
        var builder = OcrResultDTO.ContractArticles.builder();

        // 표준 계약서 조항 패턴
        Map<String, String> articlePatterns = Map.of(
                "article1", "\\(제1조.*?목적.*?\\)([^\\(]+)",
                "article2", "\\(제2조.*?(?:기간|임대차기간).*?\\)([^\\(]+)",
                "article3", "\\(제3조.*?(?:보증금|수선).*?\\)([^\\(]+)",
                "article4", "\\(제4조.*?(?:해지|해제).*?\\)([^\\(]+)",
                "article5", "\\(제5조.*?(?:갱신|연장).*?\\)([^\\(]+)",
                "article6", "\\(제6조.*?\\)([^\\(]+)",
                "article7", "\\(제7조.*?\\)([^\\(]+)"
        );

        // 각 조항 추출
        for (Map.Entry<String, String> entry : articlePatterns.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String content = matcher.group(1).trim();
                switch (entry.getKey()) {
                    case "article1" -> builder.article1(content);
                    case "article2" -> builder.article2(content);
                    case "article3" -> builder.article3(content);
                    case "article4" -> builder.article4(content);
                    case "article5" -> builder.article5(content);
                    case "article6" -> builder.article6(content);
                    case "article7" -> builder.article7(content);
                }
            }
        }

        return builder.build();
    }

    private OcrResultDTO.ContractSummary generateContractSummary(
            OcrResultDTO.ParsedContract parsedContract) {

        var builder = OcrResultDTO.ContractSummary.builder();

        // 계약 기간 요약
        if (parsedContract.getContractDetails() != null) {
            OcrResultDTO.ContractDetails details = parsedContract.getContractDetails();
            if (details.getLeaseStartDate() != null && details.getLeaseEndDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
                builder.contractPeriod(
                        details.getLeaseStartDate().format(formatter) + " ~ " +
                                details.getLeaseEndDate().format(formatter)
                );

                // 갱신 마감일 계산 (종료 6개월 전)
                LocalDate renewalDeadline = details.getLeaseEndDate().minusMonths(6);
                builder.renewalDeadline(renewalDeadline.format(formatter) + " (6개월 전)");
            }

            // 보증금 표시
            if (details.getDepositAmount() != null) {
                builder.deposit(String.format("%,d만원", details.getDepositAmount()));
            }

            // 관리비 표시
            if (details.getMaintenanceFee() != null && details.getMaintenanceFee() > 0) {
                builder.maintenanceFeeDisplay(String.format("월 %d만원", details.getMaintenanceFee()));
            }
        }

        // 주소 요약
        if (parsedContract.getPropertyInfo() != null) {
            OcrResultDTO.PropertyInfo property = parsedContract.getPropertyInfo();
            if (property.getPropertyAddress() != null) {
                // 시/구만 추출하여 간략화
                Pattern pattern = Pattern.compile("([가-힣]+(?:시|도))\\s+([가-힣]+(?:구|군))");
                Matcher matcher = pattern.matcher(property.getPropertyAddress());
                if (matcher.find()) {
                    builder.address(matcher.group(1) + " " + matcher.group(2));
                } else {
                    builder.address(property.getPropertyAddress());
                }
            }

            if (property.getDetailedAddress() != null) {
                builder.detailedAddressSummary(property.getDetailedAddress());
            }
        }

        return builder.build();
    }

    private Map<String, String> parsePartyInfo(String text) {
        Map<String, String> partyInfo = new HashMap<>();

        if (text == null || text.isEmpty()) {
            return partyInfo;
        }

        // 임대인 정보
        Pattern lessorNamePattern = Pattern.compile("임\\s*대\\s*인[:\\s]*([가-힣]{2,4})", Pattern.CASE_INSENSITIVE);
        Matcher lessorNameMatcher = lessorNamePattern.matcher(text);
        if (lessorNameMatcher.find()) {
            partyInfo.put("lessorName", lessorNameMatcher.group(1));
        }

        // 임차인 정보
        Pattern lesseeNamePattern = Pattern.compile("임\\s*차\\s*인[:\\s]*([가-힣]{2,4})", Pattern.CASE_INSENSITIVE);
        Matcher lesseeNameMatcher = lesseeNamePattern.matcher(text);
        if (lesseeNameMatcher.find()) {
            partyInfo.put("lesseeName", lesseeNameMatcher.group(1));
        }

        // 전화번호 추출
        Pattern phonePattern = Pattern.compile("(\\d{2,3}[-\\s]?\\d{3,4}[-\\s]?\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher phoneMatcher = phonePattern.matcher(text);
        List<String> phones = new ArrayList<>();
        while (phoneMatcher.find()) {
            phones.add(phoneMatcher.group(1));
        }

        if (phones.size() >= 1) partyInfo.put("lessorPhone", phones.get(0));
        if (phones.size() >= 2) partyInfo.put("lesseePhone", phones.get(1));

        // 중개업소 정보
        Pattern agentPattern = Pattern.compile("중개업소[:\\s]*([가-힣\\s]+(?:공인중개사|부동산))", Pattern.CASE_INSENSITIVE);
        Matcher agentMatcher = agentPattern.matcher(text);
        if (agentMatcher.find()) {
            partyInfo.put("agentName", agentMatcher.group(1));
        }

        // 중개업 등록번호
        Pattern licensePattern = Pattern.compile("등록번호[:\\s]*(\\d+-\\d+-\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher licenseMatcher = licensePattern.matcher(text);
        if (licenseMatcher.find()) {
            partyInfo.put("agentLicense", licenseMatcher.group(1));
        }

        return partyInfo;
    }

    /**
     * OCR 추출 텍스트를 사용자가 원하는 정확한 JSON 형식으로 변환합니다
     */
    public Map<String, Object> convertToStructuredJson(String extractedText) {
        Map<String, Object> result = new HashMap<>();

        // 1. 계약 정보 (contract_information)
        Map<String, Object> contractInformation = new HashMap<>();

        // 1-1. 부동산 상세 정보 (property_details)
        Map<String, Object> propertyDetails = parsePropertyDetailsWithKoreanLabels(extractedText);
        contractInformation.put("property_details", propertyDetails);

        // 1-2. 계약 상세 정보 (contract_details)
        Map<String, Object> contractDetails = parseContractDetailsWithKoreanLabels(extractedText);
        contractInformation.put("contract_details", contractDetails);

        result.put("contract_information", contractInformation);

        // 2. 계약 조항 (contract_articles)
        List<String> contractArticles = generateStandardArticlesWithPrefix(extractedText);
        result.put("contract_articles", contractArticles);

        // 3. 특약 사항 (special_terms)
        List<String> specialTerms = generateStandardContractTerms(extractedText);
        result.put("special_terms", specialTerms);

        return result;
    }

    /**
     * 1-1. 부동산 상세 정보 (한글 라벨 포함)
     */
    private Map<String, Object> parsePropertyDetailsWithKoreanLabels(String text) {
        Map<String, Object> propertyDetails = new HashMap<>();

        // 주소 추출
        String address = extractAddress(text);
        propertyDetails.put("property_address(부동산 소재지)", address);

        // 상세 주소
        String detailAddress = extractDetailAddress(text);
        propertyDetails.put("detailed_address(상세주소)", detailAddress);

        // 면적 (제곱미터)
        int areaSqm = extractArea(text);
        propertyDetails.put("area_sqm(면적_제곱미터)", areaSqm);

        // 평수
        double areaPyeong = Math.round(areaSqm / 3.3 * 10.0) / 10.0;
        propertyDetails.put("area_pyeong(면적_평)", areaPyeong);

        // 층수
        String floor = extractFloor(text);
        propertyDetails.put("floor(층수)", floor);

        // 건물 유형
        String buildingType = extractBuildingType(text);
        propertyDetails.put("building_type(건물유형)", buildingType);

        // 건물 구조
        String buildingStructure = extractBuildingStructure(text);
        propertyDetails.put("building_structure(건물구조)", buildingStructure);

        // 지목
        String landClassification = extractLandClassification(text);
        propertyDetails.put("land_classification(지목)", landClassification);

        // 소유권 구분
        String ownershipType = extractOwnershipType(text);
        propertyDetails.put("ownership_type(소유권구분)", ownershipType);

        return propertyDetails;
    }

    /**
     * 1-2. 계약 상세 정보 (한글 라벨 포함)
     */
    private Map<String, Object> parseContractDetailsWithKoreanLabels(String text) {
        Map<String, Object> contractDetails = new HashMap<>();

        // 계약 유형
        String contractType = text.contains("월세") ? "월세" : "전세";
        contractDetails.put("contract_type(계약유형)", contractType);

        // 계약일 추출
        String contractDate = extractContractDate(text);
        contractDetails.put("contract_date(계약체결일)", contractDate);
        contractDetails.put("lease_start_date(임대차시작일)", contractDate);

        // 계약 종료일 및 기간
        String endDate = extractLeaseEndDate(text);
        contractDetails.put("lease_end_date(임대차종료일)", endDate);

        int leasePeriod = calculateLeasePeriodMonths(contractDate, endDate);
        contractDetails.put("lease_period_months(임대차기간_개월)", leasePeriod);

        // 보증금
        int depositAmount = extractDeposit(text);
        contractDetails.put("deposit_amount(보증금)", depositAmount);

        // 월세 (월세 계약인 경우만)
        if ("월세".equals(contractType)) {
            int monthlyRent = extractMonthlyRent(text);
            contractDetails.put("monthly_rent(월세)", monthlyRent);

            // 월세 지급일
            int paymentDate = extractPaymentDate(text);
            contractDetails.put("rent_payment_date(월세지급일)", paymentDate);
        } else {
            contractDetails.put("monthly_rent(월세)", 0);
        }

        // 관리비
        int maintenanceFee = extractMaintenanceFee(text);
        contractDetails.put("maintenance_fee(관리비)", maintenanceFee);

        // 입주 가능일
        contractDetails.put("move_in_date(입주가능일)", contractDate);

        return contractDetails;
    }

    /**
     * 2. 계약 조항 추출 (실제 OCR 텍스트에서만, 개선된 패턴)
     */
    private List<String> generateStandardArticlesWithPrefix(String text) {
        List<String> articles = new ArrayList<>();

        // 제N조 패턴으로 모든 조항 찾기
        Pattern generalArticlePattern = Pattern.compile("제\\s*\\d+\\s*조[\\s\\(（:：]([^제]+?)(?=제\\s*\\d+\\s*조|\\[|특약|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher generalMatcher = generalArticlePattern.matcher(text);

        while (generalMatcher.find()) {
            String articleContent = generalMatcher.group(1).trim();
            if (articleContent.length() > 10) {
                // 조항 제목과 내용 정리
                articleContent = articleContent.replaceAll("\\s+", " ").trim();
                articles.add(articleContent);
            }
        }

        // 조항을 못 찾은 경우, 키워드 기반 검색
        if (articles.isEmpty()) {
            String[] keywords = {
                    "목적\\s*[\\)）]", "기간\\s*[\\)）]", "보증금", "해지", "갱신", "관리",
                    "임대차에 한하여", "임대차기간", "계약해지", "수선", "존속기간"
            };

            for (String keyword : keywords) {
                Pattern keywordPattern = Pattern.compile("\\(" + keyword + "([^\\(\\)]+)", Pattern.CASE_INSENSITIVE);
                Matcher keywordMatcher = keywordPattern.matcher(text);
                while (keywordMatcher.find()) {
                    String content = keywordMatcher.group(0).trim();
                    if (content.length() > 10 && !articles.contains(content)) {
                        articles.add(content);
                    }
                }
            }
        }

        return articles;
    }

    /**
     * 3. 특약사항 추출 (실제 OCR 텍스트에서만, 개선된 패턴)
     */
    private List<String> generateStandardContractTerms(String text) {
        List<String> specialTerms = new ArrayList<>();

        // 여러 패턴으로 특약사항 섹션 찾기
        String specialTermsSection = null;

        // 특약사항 시작 패턴들 (더 광범위하게)
        Pattern[] specialTermsPatterns = {
                // [ 특약사항 ] 또는 [특약사항] 형태
                Pattern.compile("\\[\\s*특\\s*약\\s*사\\s*항\\s*\\][:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                // 특약사항 제목 패턴들
                Pattern.compile("특\\s*약\\s*사\\s*항[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("-\\s*특\\s*약\\s*-[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\*\\s*특\\s*약\\s*\\*[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                // 기타사항 패턴
                Pattern.compile("기\\s*타\\s*사\\s*항[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("비\\s*고[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("참\\s*고\\s*사\\s*항[:\\s]*([\\s\\S]*?)(?:\\[.*?\\]|임\\s*대\\s*인|중개|$)", Pattern.CASE_INSENSITIVE)
        };

        // 특약사항 섹션 찾기
        for (Pattern pattern : specialTermsPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                specialTermsSection = matcher.group(1);
                break;
            }
        }

        // 특약사항 섹션을 찾지 못한 경우, 계약서 후반부 검색
        if (specialTermsSection == null || specialTermsSection.trim().length() < 10) {
            // 계약서 후반부 (마지막 1/3) 추출
            int lastThird = text.length() * 2 / 3;
            String lowerPart = text.substring(lastThird);

            // 특약사항 관련 키워드가 포함된 문장들 추출
            Pattern[] specialPatterns = {
                    Pattern.compile("([^\\n]*(?:현장\\s*답사|등기사항|건축물대장|확인\\s*후)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:현\\s*시설|현재\\s*상태|현상태)[^\\n]*계약[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*계약[^\\n]*(?:현\\s*시설|현재\\s*상태|현상태)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:에서\\s*계약|상태에서\\s*계약)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:10평|\\d+평)\\s*[^\\n]*(?:임차인|사용)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:주택임대차|임대차보호법)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:권리분석|권리관계)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:중개보수|중개수수료)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:하자|파손|수리|원상)[^\\n]*)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([^\\n]*(?:관리비|공과금|전기|수도|가스)[^\\n]*)", Pattern.CASE_INSENSITIVE)
            };

            StringBuilder foundTerms = new StringBuilder();
            for (Pattern pattern : specialPatterns) {
                Matcher matcher = pattern.matcher(lowerPart);
                while (matcher.find()) {
                    String term = matcher.group(1).trim();
                    // 조항이 아니고, 임대인/임차인 정보가 아닌 경우
                    if (term.length() > 10 &&
                            !term.contains("제") && !term.contains("조") &&
                            !term.contains("성명") && !term.contains("주소") &&
                            !term.contains("전화") && !term.contains("주민등록")) {
                        foundTerms.append(term).append("\n");
                    }
                }
            }

            if (foundTerms.length() > 0) {
                specialTermsSection = foundTerms.toString();
            }
        }

        // 특약사항 내용 파싱
        if (specialTermsSection != null && !specialTermsSection.trim().isEmpty()) {
            // 번호나 기호로 시작하는 항목들 찾기
            Pattern[] itemPatterns = {
                    Pattern.compile("(\\d+)\\s*\\.\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("(\\d+)\\s*\\)\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("\\(\\s*(\\d+)\\s*\\)\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("([①②③④⑤⑥⑦⑧⑨⑩])\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("-\\s*([^-\\n][^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("·\\s*([^·\\n][^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("•\\s*([^•\\n][^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("※\\s*([^※\\n][^\\n]+)", Pattern.CASE_INSENSITIVE),
                    Pattern.compile("○\\s*([^○\\n][^\\n]+)", Pattern.CASE_INSENSITIVE)
            };

            boolean foundItems = false;

            // 번호가 있는 패턴부터 시도
            for (int i = 0; i < 4; i++) {
                Pattern pattern = itemPatterns[i];
                Matcher matcher = pattern.matcher(specialTermsSection);

                while (matcher.find()) {
                    String content = matcher.group(2).trim();
                    if (content.length() > 5 && !content.matches("^[\\s\\p{Punct}]+$")) {
                        specialTerms.add(content);
                        foundItems = true;
                    }
                }

                if (foundItems) break;
            }

            // 번호 없는 기호 패턴 시도
            if (!foundItems) {
                for (int i = 4; i < itemPatterns.length; i++) {
                    Pattern pattern = itemPatterns[i];
                    Matcher matcher = pattern.matcher(specialTermsSection);

                    while (matcher.find()) {
                        String content = matcher.group(1).trim();
                        if (content.length() > 5) {
                            specialTerms.add(content);
                            foundItems = true;
                        }
                    }

                    if (foundItems) break;
                }
            }

            // 아직도 못 찾았으면 문장 단위로 분리
            if (!foundItems) {
                String[] lines = specialTermsSection.split("\\n+");
                for (String line : lines) {
                    line = line.trim();
                    // 유의미한 내용만 추가
                    if (line.length() > 10 &&
                            !line.matches("^[\\s\\p{Punct}]+$") &&
                            !line.contains("임대인") &&
                            !line.contains("임차인") &&
                            !line.contains("중개") &&
                            !line.contains("(인)")) {
                        specialTerms.add(line);
                    }
                }
            }
        }

        return specialTerms;
    }



    // === 개별 추출 메서드들 ===

    private String extractAddress(String text) {
        // 소재지 필드에서 주소 추출
        Pattern addressPattern = Pattern.compile("소\\s*재\\s*지[:\\s]*([^\\n]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = addressPattern.matcher(text);
        if (matcher.find()) {
            String address = matcher.group(1).trim();
            // 상세주소 제거 (호수 등)
            address = address.replaceAll("\\d+호.*$", "").trim();
            return address;
        }

        // 일반적인 주소 패턴
        Pattern generalAddressPattern = Pattern.compile("([가-힣]+(?:시|도))\\s+([가-힣]+(?:구|군))\\s+([가-힣]+(?:동|로))[^\\n]*");
        Matcher generalMatcher = generalAddressPattern.matcher(text);
        if (generalMatcher.find()) {
            return generalMatcher.group(0).trim();
        }
        return null;
    }

    private String extractDetailAddress(String text) {
        // 소재지에서 호수 추출
        Pattern detailPattern = Pattern.compile("(\\d+동\\s*\\d+호|\\d+호)");
        Matcher matcher = detailPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private int extractArea(String text) {
        // 전용면적 또는 면적 패턴 검색
        Pattern areaPattern = Pattern.compile("(?:전용|공급)?\\s*면적[:\\s]*(\\d+(?:\\.\\d+)?)\\s*(?:㎡|m2)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = areaPattern.matcher(text);
        if (matcher.find()) {
            try {
                return (int) Math.round(Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        // 일반적인 면적 패턴
        Pattern generalAreaPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:㎡|m2)");
        Matcher generalMatcher = generalAreaPattern.matcher(text);
        if (generalMatcher.find()) {
            try {
                return (int) Math.round(Double.parseDouble(generalMatcher.group(1)));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String extractFloor(String text) {
        // 층수 추출
        Pattern floorPattern = Pattern.compile("(\\d+)\\s*층");
        Matcher matcher = floorPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + "층";
        }
        return null;
    }

    private String extractBuildingType(String text) {
        if (text.contains("아파트")) return "아파트";
        if (text.contains("오피스텔")) return "오피스텔";
        if (text.contains("빌라")) return "빌라";
        if (text.contains("다세대")) return "다세대주택";
        if (text.contains("연립")) return "연립주택";
        if (text.contains("단독")) return "단독주택";
        if (text.contains("상가")) return "상가";
        return null;
    }

    private int extractDeposit(String text) {
        // 보증금 패턴들 시도 (더 광범위하게)
        Pattern[] depositPatterns = {
                // 원화 표기와 함께
                Pattern.compile("보\\s*증\\s*금[:\\s]*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("보\\s*증\\s*금[:\\s]*금\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("₩([0-9,]+).*?보\\s*증\\s*금", Pattern.CASE_INSENSITIVE),
                // 만원 단위 명시
                Pattern.compile("보\\s*증\\s*금[^\\d]*([0-9,]+)\\s*만원", Pattern.CASE_INSENSITIVE),
                Pattern.compile("보\\s*증\\s*금[^\\d]*금\\s*([0-9,]+)\\s*만원", Pattern.CASE_INSENSITIVE),
                // 천만원, 억원 단위
                Pattern.compile("보\\s*증\\s*금[^\\d]*([0-9,]+)\\s*천만원", Pattern.CASE_INSENSITIVE),
                Pattern.compile("보\\s*증\\s*금[^\\d]*([0-9,]+)\\s*억", Pattern.CASE_INSENSITIVE),
                // 일반적인 숫자 패턴
                Pattern.compile("보\\s*증\\s*금[^\\d]*([0-9,]+)", Pattern.CASE_INSENSITIVE),
                // 괄호 안의 숫자
                Pattern.compile("보\\s*증\\s*금[^\\(]*\\(\\s*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\(\\s*₩([0-9,]+)\\s*\\)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : depositPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    String amountStr = matcher.group(1).replaceAll(",", "");
                    long amount = Long.parseLong(amountStr);

                    // 패턴에 따라 단위 판단
                    if (pattern.pattern().contains("만원")) {
                        return (int) amount; // 이미 만원 단위
                    } else if (pattern.pattern().contains("천만원")) {
                        return (int) (amount * 1000); // 천만원 -> 만원 단위
                    } else if (pattern.pattern().contains("억")) {
                        return (int) (amount * 10000); // 억원 -> 만원 단위
                    } else if (amount > 100000) { // 10만원 이상이면 원 단위로 판단
                        return (int) (amount / 10000); // 만원 단위로 변환
                    } else {
                        return (int) amount; // 이미 만원 단위로 추정
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return 0; // 찾지 못한 경우 0 반환
    }

    private int extractMonthlyRent(String text) {
        Pattern[] rentPatterns = {
                Pattern.compile("월\\s*세[:\\s]*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("월\\s*세[:\\s]*금\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("월\\s*세[^\\d]*([0-9,]+)만원", Pattern.CASE_INSENSITIVE),
                Pattern.compile("월\\s*세[^\\d]*([0-9,]+)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : rentPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    String rentStr = matcher.group(1).replaceAll(",", "");
                    int rent = Integer.parseInt(rentStr);
                    // 만원 단위가 아니면 변환
                    if (rent > 10000) { // 1만원 이상이면 원 단위로 판단
                        return rent / 10000;
                    }
                    return rent;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return 0; // 월세가 없으면 0 (전세인 경우)
    }

    private int extractPaymentDate(String text) {
        Pattern[] paymentPatterns = {
                Pattern.compile("매월\\s*(\\d{1,2})일", Pattern.CASE_INSENSITIVE),
                Pattern.compile("월세.*?(\\d{1,2})일", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(\\d{1,2})일.*?납부", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : paymentPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return 0; // 찾지 못한 경우 0
    }

    private int extractMaintenanceFee(String text) {
        Pattern[] maintenancePatterns = {
                Pattern.compile("관\\s*리\\s*비[:\\s]*₩([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("관\\s*리\\s*비[:\\s]*금\\s*([0-9,]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("관\\s*리\\s*비[^\\d]*([0-9,]+)만원", Pattern.CASE_INSENSITIVE),
                Pattern.compile("관\\s*리\\s*비[^\\d]*([0-9,]+)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : maintenancePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    String feeStr = matcher.group(1).replaceAll(",", "");
                    int fee = Integer.parseInt(feeStr);
                    // 만원 단위가 아니면 변환
                    if (fee > 10000) { // 1만원 이상이면 원 단위로 판단
                        return fee / 10000;
                    }
                    return fee;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        return 0; // 찾지 못한 경우 0
    }

    // === 새로 추가된 추출 메서드들 ===

    private String extractBuildingStructure(String text) {
        if (text.contains("철근콘크리트")) return "철근콘크리트";
        if (text.contains("벽돌조")) return "벽돌조";
        if (text.contains("목조")) return "목조";
        if (text.contains("철골조")) return "철골조";
        return null;
    }

    private String extractOwnershipType(String text) {
        if (text.contains("집합건물")) return "집합건물";
        if (text.contains("단독소유")) return "단독소유";
        return null;
    }

    private String extractLandClassification(String text) {
        if (text.contains("대지")) return "대지";
        if (text.contains("전")) return "전";
        if (text.contains("답")) return "답";
        if (text.contains("임야")) return "임야";
        if (text.contains("잡종지")) return "잡종지";
        return null;
    }

    private String extractContractDate(String text) {
        // 계약일 또는 계약체결일 패턴
        Pattern[] contractDatePatterns = {
                Pattern.compile("계\\s*약\\s*(?:체결)?\\s*일[:\\s]*(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일", Pattern.CASE_INSENSITIVE),
                Pattern.compile("계\\s*약\\s*(?:체결)?\\s*일[:\\s]*(\\d{4})[.-](\\d{1,2})[.-](\\d{1,2})", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일"),
                Pattern.compile("(\\d{4})[.-](\\d{1,2})[.-](\\d{1,2})")
        };

        for (Pattern pattern : contractDatePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return String.format("%s-%02d-%02d",
                        matcher.group(1),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)));
            }
        }
        return null;
    }

    private String extractLeaseEndDate(String text) {
        // 임대차기간에서 종료일 추출
        Pattern[] endDatePatterns = {
                Pattern.compile("임대차.*?기간[:\\s]*(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일.*?부터.*?(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일.*?까지", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일.*?부터.*?(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일.*?까지"),
                Pattern.compile("임대차.*?종료[:\\s]*(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일", Pattern.CASE_INSENSITIVE),
                Pattern.compile("종료.*?일[:\\s]*(\\d{4})년\\s*(\\d{1,2})월\\s*(\\d{1,2})일", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : endDatePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                if (matcher.groupCount() >= 6) {
                    // 시작일과 종료일이 모두 있는 경우 (종료일 사용)
                    return String.format("%s-%02d-%02d",
                            matcher.group(4),
                            Integer.parseInt(matcher.group(5)),
                            Integer.parseInt(matcher.group(6)));
                } else {
                    // 종료일만 있는 경우
                    return String.format("%s-%02d-%02d",
                            matcher.group(1),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)));
                }
            }
        }
        return null;
    }

    private int calculateLeasePeriodMonths(String startDate, String endDate) {
        try {
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");

            int startYear = Integer.parseInt(startParts[0]);
            int startMonth = Integer.parseInt(startParts[1]);
            int endYear = Integer.parseInt(endParts[0]);
            int endMonth = Integer.parseInt(endParts[1]);

            return (endYear - startYear) * 12 + (endMonth - startMonth);
        } catch (Exception e) {
            return 12; // 기본값 (1년)
        }
    }
}