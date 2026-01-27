package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceHistoryRowDto;
import com.getAssetsPortal.dto.UserAssetResponseDto;
import com.getAssetsPortal.dto.UserDetailDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    @Override
    public byte[] exportUserAssets(List<UserAssetResponseDto> assets) {
        return new byte[0];
    }

    @Override
    public byte[] exportDeviceHistory(DeviceHistoryResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Device History");

            String[] columns = {
                    "Serial No",
                    "IMEI",
                    "Asset Code",
                    "Domain ID",
                    "Employee Code",
                    "Assigned To",
                    "Used By",
                    "Action",
                    "Remark",
                    "Action Date",
                    "Action By"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (DeviceHistoryRowDto rowDto : response.getHistory()) {

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(rowDto.getSerialNumber());
                row.createCell(1).setCellValue(rowDto.getImei());
                row.createCell(2).setCellValue(rowDto.getAssetCode());

                row.createCell(3).setCellValue(rowDto.getDomainId());
                row.createCell(4).setCellValue(rowDto.getEmployeeCode());
                row.createCell(5).setCellValue(rowDto.getAssignedTo());
                row.createCell(6).setCellValue(rowDto.getUsedBy());

                row.createCell(7).setCellValue(rowDto.getAction());
                row.createCell(8).setCellValue(rowDto.getRemark());

                row.createCell(9).setCellValue(
                        rowDto.getActionDate() != null
                                ? rowDto.getActionDate().toString()
                                : ""
                );

                row.createCell(10).setCellValue(rowDto.getActionBy());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export device history", e);
        }
    }

}