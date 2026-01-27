package com.getAssetsPortal.services.export;

import com.getAssetsPortal.dto.DeviceHistoryResponse;
import com.getAssetsPortal.dto.DeviceHistoryRowDto;
import com.getAssetsPortal.dto.UserAssetResponseDto;
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
    public byte[] exportUserAssets(List<com.getAssetsPortal.dto.UserAssetRowDto> assets) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("User Assets");

            String[] columns = {
                    "Serial Number", "Brand", "Model", "Device Type", "Device SubType",
                    "Host Name", "IMEI", "MAC ID", "Asset Controlled By",
                    "Assigned To", "Used By", "Remark",
                    "Assigned Date", "Installed Date", "Assigned By"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;

            for (com.getAssetsPortal.dto.UserAssetRowDto asset : assets) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(asset.getSerialNumber());
                row.createCell(1).setCellValue(asset.getBrand());
                row.createCell(2).setCellValue(asset.getModel());
                row.createCell(3).setCellValue(asset.getDeviceType());
                row.createCell(4).setCellValue(asset.getDeviceSubType());
                row.createCell(5).setCellValue(asset.getHostName());
                row.createCell(6).setCellValue(asset.getImei());
                row.createCell(7).setCellValue(asset.getMacId());
                row.createCell(8).setCellValue(asset.getAssetControlledBy());
                row.createCell(9).setCellValue(asset.getAssignedTo());
                row.createCell(10).setCellValue(asset.getUsedBy());
                row.createCell(11).setCellValue(asset.getRemark());

                row.createCell(12)
                        .setCellValue(asset.getAssignedDate() != null ? asset.getAssignedDate().toString() : "");
                row.createCell(13)
                        .setCellValue(asset.getInstalledDate() != null ? asset.getInstalledDate().toString() : "");
                row.createCell(14).setCellValue(asset.getAssignedBy());
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
                                : "");

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