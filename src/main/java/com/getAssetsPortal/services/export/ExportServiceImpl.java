package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceHistoryRowDto;
import com.getAssetsPortal.dto.UserAssetDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    @Override
    public byte[] exportUserAssets(List<UserAssetDto> assets) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("User Assets");

            Row header = sheet.createRow(0);
            String[] columns = {
                    "Domain ID", "Employee Code", "Device Type", "Sub Type",
                    "Brand", "Model", "Serial No", "Host Name",
                    "Assigned Date", "Assigned By", "Status"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (UserAssetDto dto : assets) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getDomainId());
                row.createCell(1).setCellValue(dto.getEmployeeCode());
                row.createCell(2).setCellValue(dto.getDeviceType());
                row.createCell(3).setCellValue(dto.getDeviceSubType());
                row.createCell(4).setCellValue(dto.getBrand());
                row.createCell(5).setCellValue(dto.getModel());
                row.createCell(6).setCellValue(dto.getSerialNumber());
                row.createCell(7).setCellValue(dto.getHostName());
                row.createCell(8).setCellValue(
                        dto.getAssignedDate() != null ? dto.getAssignedDate().toString() : ""
                );
                row.createCell(9).setCellValue(dto.getAssignedBy());
                row.createCell(10).setCellValue("ACTIVE");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export user assets", e);
        }
    }

    @Override
    public byte[] exportDeviceHistory(DeviceHistoryResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Device History");

            Row header = sheet.createRow(0);
            String[] columns = {
                    "Domain ID", "Employee Code", "Assigned On",
                    "Unassigned On", "Assigned By"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (DeviceHistoryRowDto dto : response.getHistory()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getDomainId());
                row.createCell(1).setCellValue(dto.getEmployeeCode());
                row.createCell(2).setCellValue(
                        dto.getAssignedOn() != null ? dto.getAssignedOn().toString() : ""
                );
                row.createCell(3).setCellValue(
                        dto.getUnassignedOn() != null ? dto.getUnassignedOn().toString() : ""
                );
                row.createCell(4).setCellValue(dto.getAssignedBy());
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