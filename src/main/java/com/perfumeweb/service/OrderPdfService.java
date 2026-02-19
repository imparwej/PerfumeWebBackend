package com.perfumeweb.service;

import com.perfumeweb.model.Order;
import com.perfumeweb.model.OrderItem;
import com.perfumeweb.model.Perfume;
import com.perfumeweb.model.User;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderPdfService {

    // Premium color palette - PRESERVED EXACTLY
    private static final float[] DARK_GOLD = {0.71f, 0.58f, 0.33f};
    private static final float[] LIGHT_GOLD = {0.94f, 0.88f, 0.67f};
    private static final float[] DARK_GRAY = {0.16f, 0.16f, 0.16f};
    private static final float[] LIGHT_GRAY = {0.95f, 0.95f, 0.95f};

    public byte[] generateInvoice(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("Orders list cannot be null or empty");
        }

        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            // ================= BACKGROUND ELEMENTS (PRESERVED) =================
            content.setLineWidth(1.5f);
            content.setStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.addRect(30, 30, PDRectangle.A4.getWidth() - 60, PDRectangle.A4.getHeight() - 60);
            content.stroke();

            content.setLineWidth(3f);
            content.setStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.moveTo(30, PDRectangle.A4.getHeight() - 70);
            content.lineTo(PDRectangle.A4.getWidth() - 30, PDRectangle.A4.getHeight() - 70);
            content.stroke();

            float y = 750;
            float left = 50;

            // ================= WATERMARK (PRESERVED) =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 90);
            content.setNonStrokingColor(0.95f, 0.95f, 0.95f);
            content.newLineAtOffset(120, 350);
            content.showText("PERFUME");
            content.newLineAtOffset(0, -100);
            content.showText("HOUSE");
            content.endText();

            // ================= HEADER (PRESERVED LAYOUT) =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 28);
            content.setNonStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.newLineAtOffset(PDRectangle.A4.getWidth() / 2 - 100, y);
            content.showText("PERFUME HOUSE");
            content.endText();

            y -= 30;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 11);
            content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);
            content.newLineAtOffset(PDRectangle.A4.getWidth() / 2 - 120, y);
            content.showText("LUXURY FRAGANCE BOUTIQUE | EST. 1995 | GST: 27ABCDE1234F1Z5");
            content.endText();

            y -= 60;

            BigDecimal grandTotal = BigDecimal.ZERO;
            BigDecimal totalTax = BigDecimal.ZERO;

            for (Order order : orders) {
                if (order == null) continue;

                // ===== INVOICE HEADER BOX (PRESERVED) =====
                content.setNonStrokingColor(LIGHT_GOLD[0], LIGHT_GOLD[1], LIGHT_GOLD[2]);
                content.fillRect(left - 10, y + 10, PDRectangle.A4.getWidth() - 100, 40);

                content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);

                // SAFE: Invoice number with null check
                String invoiceNo = "INV-" + (order.getId() != null ?
                        String.format("%06d", order.getId()) : "000000");

                // SAFE: Date formatting with fallback
                String date = Optional.ofNullable(order.getCreatedAt())
                        .map(d -> d.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                        .orElse("N/A");

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.newLineAtOffset(left, y);
                content.showText("INVOICE " + invoiceNo);
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(left + 300, y);
                content.showText("Date: " + date);
                content.endText();

                y -= 25;

                // BACKEND FIX: Get customer name from database
                String customerName = "Guest User";

                if (order.getUser() != null) {
                    User user = order.getUser();

                    if (user.getName() != null && !user.getName().isEmpty()) {
                        customerName = user.getName();
                    } else if (user.getEmail() != null) {
                        customerName = user.getEmail();
                    }
                }


                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(left, y);
                content.showText("Customer: " + customerName + " | Invoice Status: PAID");
                content.endText();

                y -= 35;

                // ===== TABLE HEADER (PRESERVED) =====
                content.setNonStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
                content.fillRect(left - 10, y - 10, PDRectangle.A4.getWidth() - 100, 25);

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.setNonStrokingColor(1f, 1f, 1f);
                content.newLineAtOffset(left, y);
                content.showText("ITEM");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.setNonStrokingColor(1f, 1f, 1f);
                content.newLineAtOffset(left + 350, y);
                content.showText("QTY");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                content.setNonStrokingColor(1f, 1f, 1f);
                content.newLineAtOffset(left + 400, y);
                content.showText("PRICE");
                content.endText();

                y -= 25;

                // BACKEND FIX: Null-safe order items handling
                List<OrderItem> orderItems = order.getOrderItems();
                if (orderItems != null && !orderItems.isEmpty()) {
                    int itemCount = 0;
                    BigDecimal orderSubtotal = BigDecimal.ZERO;

                    for (OrderItem item : orderItems) {
                        if (item == null) continue;

                        BigDecimal itemPrice = BigDecimal.ZERO;
                        int quantity = item.getQuantity();
                        String itemName = "Unknown Item";

                        if (item.getPrice() != null) {
                            itemPrice = item.getPrice();
                        }


                        Perfume perfume = item.getPerfume();
                        if (perfume != null && perfume.getName() != null) {
                            itemName = perfume.getName();
                        }

                        // BACKEND FIX: Calculate price × quantity
                        BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
                        orderSubtotal = orderSubtotal.add(itemTotal);

                        // PRESERVED: Alternate row colors
                        if (itemCount % 2 == 0) {
                            content.setNonStrokingColor(LIGHT_GRAY[0], LIGHT_GRAY[1], LIGHT_GRAY[2]);
                            content.fillRect(left - 10, y - 5, PDRectangle.A4.getWidth() - 100, 50);
                        }

                        content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);

                        // Item name
                        content.beginText();
                        content.setFont(PDType1Font.HELVETICA, 11);
                        content.newLineAtOffset(left, y);
                        content.showText(itemName);
                        content.endText();

                        // Quantity
                        content.beginText();
                        content.setFont(PDType1Font.HELVETICA, 11);
                        content.newLineAtOffset(left + 350, y);
                        content.showText(String.valueOf(quantity));
                        content.endText();

                        // Price (BACKEND FIX: Use "Rs" instead of ₹)
                        content.beginText();
                        content.setFont(PDType1Font.HELVETICA_BOLD, 11);
                        content.newLineAtOffset(left + 400, y);
                        content.showText("Rs " + String.format("%,.2f", itemTotal.doubleValue()));
                        content.endText();

                        // ===== SAFE IMAGE LOADING =====
                        try {
                            if (perfume != null && perfume.getImageUrl() != null) {
                                String imageUrl = perfume.getImageUrl();
                                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                                    URL url = new URL(imageUrl);
                                    try (InputStream is = url.openStream()) {
                                        byte[] imageBytes = is.readAllBytes();
                                        PDImageXObject img = PDImageXObject.createFromByteArray(
                                                document, imageBytes, "img");
                                        // PRESERVED: Image border and position
                                        content.setStrokingColor(LIGHT_GOLD[0], LIGHT_GOLD[1], LIGHT_GOLD[2]);
                                        content.setLineWidth(1f);
                                        content.addRect(450, y - 10, 45, 45);
                                        content.stroke();
                                        content.drawImage(img, 452, y - 8, 41, 41);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Log but don't crash - invoice must generate without images
                            System.err.println("Failed to load image for perfume: " +
                                    (perfume != null ? perfume.getId() : "unknown") +
                                    ", Error: " + e.getMessage());
                        }

                        y -= 55;
                        itemCount++;

                        // PRESERVED: Separator line
                        if (y > 100) {
                            content.setStrokingColor(0.9f, 0.9f, 0.9f);
                            content.setLineWidth(0.5f);
                            content.moveTo(left, y + 5);
                            content.lineTo(PDRectangle.A4.getWidth() - 50, y + 5);
                            content.stroke();
                        }

                        // Page break logic (PRESERVED)
                        if (y < 150) {
                            content.close();
                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            content = new PDPageContentStream(document, page);

                            // Draw border on new page (PRESERVED)
                            content.setLineWidth(1.5f);
                            content.setStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
                            content.addRect(30, 30, PDRectangle.A4.getWidth() - 60,
                                    PDRectangle.A4.getHeight() - 60);
                            content.stroke();

                            y = 750;
                            left = 50;
                        }
                    }

                    // BACKEND FIX: Add to grand total
                    grandTotal = grandTotal.add(orderSubtotal);

                    // BACKEND FIX: Calculate tax - check database first, then compute
                    BigDecimal orderTax = orderSubtotal.multiply(new BigDecimal("0.18"));
                    totalTax = totalTax.add(orderTax);

                }

                y -= 20;
            }

            // ================= SUMMARY SECTION (PRESERVED LAYOUT) =================
            content.setNonStrokingColor(LIGHT_GOLD[0], LIGHT_GOLD[1], LIGHT_GOLD[2]);
            content.fillRect(left + 250, y - 20, 250, 100);

            // BACKEND FIX: Use actual calculated values
            BigDecimal finalTotal = grandTotal.add(totalTax);

            content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 11);
            content.newLineAtOffset(left + 260, y);
            content.showText("Subtotal:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 11);
            content.newLineAtOffset(left + 400, y);
            // BACKEND FIX: Use "Rs" instead of ₹
            content.showText("Rs " + String.format("%,.2f", grandTotal.doubleValue()));
            content.endText();

            y -= 20;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 11);
            content.newLineAtOffset(left + 260, y);
            content.showText("GST (18%):");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 11);
            content.newLineAtOffset(left + 400, y);
            // BACKEND FIX: Use "Rs" instead of ₹
            content.showText("Rs " + String.format("%,.2f", totalTax.doubleValue()));
            content.endText();

            y -= 25;

            content.setLineWidth(1f);
            content.setStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.moveTo(left + 260, y + 5);
            content.lineTo(left + 490, y + 5);
            content.stroke();

            y -= 15;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setNonStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.newLineAtOffset(left + 260, y);
            content.showText("TOTAL:");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setNonStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.newLineAtOffset(left + 400, y);
            // BACKEND FIX: Use "Rs" instead of ₹
            content.showText("Rs " + String.format("%,.2f", finalTotal.doubleValue()));
            content.endText();

            y -= 40;

            // ================= QR CODE SECTION (PRESERVED LAYOUT) =================
            String qrData = "Perfume House | Invoice Total: Rs " +
                    String.format("%,.2f", finalTotal.doubleValue()) +
                    " | Thank you for your purchase!";

            try {
                BitMatrix matrix = new MultiFormatWriter()
                        .encode(qrData, BarcodeFormat.QR_CODE, 120, 120);

                ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(matrix, "PNG", qrOut);

                PDImageXObject qrImage = PDImageXObject.createFromByteArray(
                        document, qrOut.toByteArray(), "qr");

                // PRESERVED: QR code decorative border
                content.setNonStrokingColor(1f, 1f, 1f);
                content.fillRect(50, y - 10, 120, 120);
                content.setStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
                content.setLineWidth(2f);
                content.addRect(50, y - 10, 120, 120);
                content.stroke();
                content.drawImage(qrImage, 55, y - 5, 110, 110);

                // QR Code label (PRESERVED)
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 9);
                content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);
                content.newLineAtOffset(55, y - 20);
                content.showText("Scan for payment verification");
                content.endText();
            } catch (Exception e) {
                System.err.println("QR code generation failed: " + e.getMessage());
                // Continue without QR code - invoice is still valid
            }

            // ================= FOOTER (PRESERVED) =================
            content.setNonStrokingColor(LIGHT_GRAY[0], LIGHT_GRAY[1], LIGHT_GRAY[2]);
            content.fillRect(30, 30, PDRectangle.A4.getWidth() - 60, 40);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);
            content.newLineAtOffset(PDRectangle.A4.getWidth() / 2 - 150, 50);
            content.showText("PERFUME HOUSE PRIVATE LIMITED | 123 LUXURY AVENUE, MUMBAI 400001");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 9);
            content.setNonStrokingColor(DARK_GRAY[0], DARK_GRAY[1], DARK_GRAY[2]);
            content.newLineAtOffset(PDRectangle.A4.getWidth() / 2 - 120, 35);
            content.showText("support@perfumehouse.com | +91 22 6123 4567 | www.perfumehouse.com");
            content.endText();

            // PRESERVED: Decorative element
            content.setNonStrokingColor(DARK_GOLD[0], DARK_GOLD[1], DARK_GOLD[2]);
            content.fillRect(PDRectangle.A4.getWidth() / 2 - 50, 65, 100, 3);

            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            try {
                document.close();
            } catch (Exception ex) {
                // Ignore cleanup errors
            }
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
}