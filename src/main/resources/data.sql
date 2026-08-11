-- =========================
-- CATEGORIES
-- =========================

INSERT INTO categories (id, name, description) VALUES
                                                   (1, 'Medicines', 'Medicinal products'),
                                                   (2, 'Cosmetics', 'Cosmetic and beauty products'),
                                                   (3, 'Supplements', 'Food and vitamin supplements'),
                                                   (4, 'Hygiene', 'Personal hygiene products'),
                                                   (5, 'General', 'General pharmacy items');

-- =========================
-- COMMON PRODUCTS (IDs 1 to 30)
-- SKU FORMAT: {CATEGORY_PREFIX}-{NAME_PREFIX}-{UUID}
-- =========================

INSERT INTO products (
    id, name, description, manufacturer, cost_price, original_price, promotional_price, promotion_start_date, promotion_end_date,
    active, sku, barcode, product_type,
    category_id, created_at, updated_at
) VALUES
      (1, 'Cotton Swabs', 'Flexible cotton swabs', 'Johnson & Johnson', 3.54, 7.08, NULL, NULL, NULL, true, 'HYG-COT-A1B2', '789100000001', 'COMMON', 4, NOW(), NOW()),
      (2, 'Dental Floss', 'Mint dental floss', 'Colgate', 5.10, 10.20, NULL, NULL, NULL, true, 'HYG-DEN-C3D4', '789100000002', 'COMMON', 4, NOW(), NOW()),
      (3, 'Vitamin C Supplement', 'Immune support supplement', 'Centrum', 17.94, 35.88, NULL, NULL, NULL, true, 'SUP-VIT-E5F6', '789100000003', 'COMMON', 3, NOW(), NOW()),
      (4, 'Protein Bar', 'Chocolate protein bar', 'Max Titanium', 7.79, 15.59, NULL, NULL, NULL, true, 'SUP-PRO-G7H8', '789100000004', 'COMMON', 3, NOW(), NOW()),
      (5, 'Face Cream', 'Hydrating facial cream', 'Nivea', 21.00, 42.00, NULL, NULL, NULL, true, 'COS-FAC-I9J0', '789100000005', 'COMMON', 2, NOW(), NOW()),
      (6, 'Sunscreen SPF 50', 'Body sunscreen', 'La Roche-Posay', 47.94, 95.88, NULL, NULL, NULL, true, 'COS-SUN-K1L2', '789100000006', 'COMMON', 2, NOW(), NOW()),
      (7, 'Hand Sanitizer', '70% alcohol sanitizer', 'Asseptgel', 5.99, 11.99, NULL, NULL, NULL, true, 'GEN-HAN-M3N4', '789100000007', 'COMMON', 5, NOW(), NOW()),
      (8, 'Bandage Pack', 'Adhesive bandages', 'Cremer', 8.94, 17.88, NULL, NULL, NULL, true, 'GEN-BAN-O5P6', '789100000008', 'COMMON', 5, NOW(), NOW()),
      (9, 'Shampoo Anti Dandruff', 'Anti dandruff shampoo', 'Clear', 13.50, 27.00, NULL, NULL, NULL, false, 'COS-SHA-Q7R8', '789100000009', 'COMMON', 2, NOW(), NOW()),
      (10, 'Moisturizing Soap', 'Skin moisturizing soap', 'Dove', 4.19, 8.39, NULL, NULL, NULL, true, 'HYG-MOI-S9T0', '789100000010', 'COMMON', 4, NOW(), NOW()),
      (11, 'Toothbrush Soft', 'Soft bristle toothbrush', 'Oral-B', 9.30, 18.60, NULL, NULL, NULL, true, 'HYG-TOO-U1V2', '789100000011', 'COMMON', 4, NOW(), NOW()),
      (12, 'Mouthwash Mint', 'Antiseptic mouthwash', 'Listerine', 15.54, 31.08, NULL, NULL, NULL, true, 'HYG-MOU-W3X4', '789100000012', 'COMMON', 4, NOW(), NOW()),
      (13, 'Body Lotion', 'Daily body lotion', 'Cerave', 53.94, 107.88, NULL, NULL, NULL, true, 'COS-BOD-Y5Z6', '789100000013', 'COMMON', 2, NOW(), NOW()),
      (14, 'Lip Balm', 'Cherry lip balm', 'Nivea', 10.80, 21.60, NULL, NULL, NULL, true, 'COS-LIP-A7B8', '789100000014', 'COMMON', 2, NOW(), NOW()),
      (15, 'Whey Protein Isolate', 'Vanilla whey protein 900g', 'IntegralMedica', 89.94, 179.88, NULL, NULL, NULL, true, 'SUP-WHE-C9D0', '789100000015', 'COMMON', 3, NOW(), NOW()),
      (16, 'Multivitamin Daily', 'Daily vitamin complex', 'Lavitan', 23.94, 47.88, NULL, NULL, NULL, true, 'SUP-MUL-E1F2', '789100000016', 'COMMON', 3, NOW(), NOW()),
      (17, 'Creatine Powder', 'Pure creatine 300g', 'Growth', 59.94, 119.88, NULL, NULL, NULL, true, 'SUP-CRE-G3H4', '789100000017', 'COMMON', 3, NOW(), NOW()),
      (18, 'Hair Conditioner', 'Repairing hair conditioner', 'Pantene', 14.70, 29.40, NULL, NULL, NULL, true, 'COS-HAI-I5J6', '789100000018', 'COMMON', 2, NOW(), NOW()),
      (19, 'Deodorant Roll-on', 'Invisible deodorant', 'Rexona', 10.14, 20.28, NULL, NULL, NULL, true, 'HYG-DEO-K7L8', '789100000019', 'COMMON', 4, NOW(), NOW()),
      (20, 'Shaving Cream', 'Sensitive skin shaving cream', 'Gillette', 17.94, 35.88, NULL, NULL, NULL, true, 'HYG-SHA-M9N0', '789100000020', 'COMMON', 4, NOW(), NOW()),
      (21, 'Nail Clippers', 'Stainless steel clippers', 'Trim', 7.20, 14.40, NULL, NULL, NULL, true, 'GEN-NAI-O1P2', '789100000021', 'COMMON', 5, NOW(), NOW()),
      (22, 'Thermometer Digital', 'Digital body thermometer', 'G-Tech', 21.00, 42.00, NULL, NULL, NULL, true, 'GEN-THE-Q3R4', '789100000022', 'COMMON', 5, NOW(), NOW()),
      (23, 'First Aid Kit', 'Basic first aid items', 'Cremer', 33.54, 67.08, NULL, NULL, NULL, true, 'GEN-FIR-S5T6', '789100000023', 'COMMON', 5, NOW(), NOW()),
      (24, 'Eye Drops Lubricant', 'Dry eye relief', 'Moura Brasil', 13.20, 26.40, NULL, NULL, NULL, true, 'GEN-EYE-U7V8', '789100000024', 'COMMON', 5, NOW(), NOW()),
      (25, 'Ear Plugs', 'Foam ear plugs', 'Mack s', 11.94, 23.88, 19.90, NULL, NULL, true, 'GEN-EAR-W9X0', '789100000025', 'COMMON', 5, NOW(), NOW()),
      (26, 'Wet Wipes', 'Baby wet wipes 100 units', 'Huggies', 8.70, 17.40, NULL, NULL, NULL, true, 'HYG-WET-Y1Z2', '789100000026', 'COMMON', 4, NOW(), NOW()),
      (27, 'Baby Powder', 'Talc-free baby powder', 'Granado', 13.14, 26.28, NULL, NULL, NULL, true, 'HYG-BAB-A3B4', '789100000027', 'COMMON', 4, NOW(), NOW()),
      (28, 'Makeup Remover', 'Micellar water 200ml', 'Garnier', 17.34, 34.68, NULL, NULL, NULL, true, 'COS-MAK-C5D6', '789100000028', 'COMMON', 2, NOW(), NOW()),
      (29, 'Omega 3 Fish Oil', 'Omega 3 1000mg', 'Sundown', 45.00, 90.00, NULL, NULL, NULL, true, 'SUP-OME-E7F8', '789100000029', 'COMMON', 3, NOW(), NOW()),
      (30, 'Magnesium Citrate', 'Magnesium supplement', 'Now Foods', 39.54, 79.08, NULL, NULL, NULL, true, 'SUP-MAG-G9H0', '789100000030', 'COMMON', 3, NOW(), NOW());

-- =========================
-- MEDICINE PRODUCTS (IDs 31 to 50)
-- SKU FORMAT: {CATEGORY_PREFIX}-{NAME_PREFIX}-{DOSAGE_PREFIX}-{PRESENTATION_PREFIX}-{UUID}
-- =========================

INSERT INTO products (
    id, name, description, manufacturer, cost_price, original_price, promotional_price, promotion_start_date, promotion_end_date,
    active, sku, barcode, product_type,
    category_id, created_at, updated_at
) VALUES
      (31, 'Dipyrone 500mg', 'Pain and fever relief', 'Neo Quimica', 9.54, 19.08, NULL, NULL, NULL, true, 'MED-DIP-500-TAB-I1J2', '789100000031', 'MEDICINE', 1, NOW(), NOW()),
      (32, 'Ibuprofen 600mg', 'Anti inflammatory medicine', 'Medley', 11.10, 22.20, NULL, NULL, NULL, true, 'MED-IBU-600-TAB-K3L4', '789100000032', 'MEDICINE', 1, NOW(), NOW()),
      (33, 'Amoxicillin 500mg', 'Antibiotic medicine', 'EMS', 19.74, 39.48, NULL, NULL, NULL, true, 'MED-AMO-500-CAP-M5N6', '789100000033', 'MEDICINE', 1, NOW(), NOW()),
      (34, 'Loratadine Syrup', 'Antihistamine syrup', 'Cimed', 13.14, 26.28, NULL, NULL, NULL, true, 'MED-LOR-100-SYR-O7P8', '789100000034', 'MEDICINE', 1, NOW(), NOW()),
      (35, 'Paracetamol 750mg', 'Pain and fever medicine', 'Eurofarma', 8.39, 16.79, NULL, NULL, NULL, true, 'MED-PAR-750-TAB-Q9R0', '789100000035', 'MEDICINE', 1, NOW(), NOW()),
      (36, 'Diclofenac Cream', 'Muscle pain cream', 'EMS', 15.00, 30.00, NULL, NULL, NULL, true, 'MED-DIC-50-CRE-S1T2', '789100000036', 'MEDICINE', 1, NOW(), NOW()),
      (37, 'Azithromycin 500mg', 'Antibiotic tablets', 'Medley', 29.94, 59.88, NULL, NULL, NULL, false, 'MED-AZI-500-TAB-U3V4', '789100000037', 'MEDICINE', 1, NOW(), NOW()),
      (38, 'Cetirizine Drops', 'Antiallergic drops', 'Neo Quimica', 11.94, 23.88, NULL, NULL, NULL, true, 'MED-CET-20-DRO-W5X6', '789100000038', 'MEDICINE', 1, NOW(), NOW()),
      (39, 'Omeprazole 20mg', 'Gastric protector', 'Teuto', 16.74, 33.48, NULL, NULL, NULL, true, 'MED-OME-20-CAP-Y7Z8', '789100000039', 'MEDICINE', 1, NOW(), NOW()),
      (40, 'Prednisone 20mg', 'Anti inflammatory corticosteroid', 'EMS', 18.90, 37.80, NULL, NULL, NULL, true, 'MED-PRE-20-TAB-A9B0', '789100000040', 'MEDICINE', 1, NOW(), NOW()),
      (41, 'Losartan 50mg', 'Blood pressure medicine', 'Neo Quimica', 7.50, 15.00, NULL, NULL, NULL, true, 'MED-LOS-50-TAB-C1D2', '789100000041', 'MEDICINE', 1, NOW(), NOW()),
      (42, 'Simvastatin 20mg', 'Cholesterol medicine', 'Medley', 10.14, 20.28, NULL, NULL, NULL, true, 'MED-SIM-20-TAB-E3F4', '789100000042', 'MEDICINE', 1, NOW(), NOW()),
      (43, 'Metformin 850mg', 'Diabetes medicine', 'Eurofarma', 8.94, 17.88, NULL, NULL, NULL, true, 'MED-MET-850-TAB-G5H6', '789100000043', 'MEDICINE', 1, NOW(), NOW()),
      (44, 'Aspirin 100mg', 'Heart health support', 'Bayer', 12.00, 24.00, NULL, NULL, NULL, true, 'MED-ASP-100-TAB-I7J8', '789100000044', 'MEDICINE', 1, NOW(), NOW()),
      (45, 'Enalapril 10mg', 'Blood pressure medicine', 'Teuto', 5.94, 11.88, NULL, NULL, NULL, true, 'MED-ENA-10-TAB-K9L0', '789100000045', 'MEDICINE', 1, NOW(), NOW()),
      (46, 'Clonazepam 2mg', 'Anxiolytic medicine', 'EMS', 13.50, 27.00, NULL, NULL, NULL, true, 'MED-CLO-2-TAB-M1N2', '789100000046', 'MEDICINE', 1, NOW(), NOW()),
      (47, 'Fluoxetine 20mg', 'Antidepressant', 'Medley', 17.94, 35.88, NULL, NULL, NULL, true, 'MED-FLU-20-CAP-O3P4', '789100000047', 'MEDICINE', 1, NOW(), NOW()),
      (48, 'Salbutamol Inhaler', 'Asthma relief', 'GSK', 27.00, 54.00, NULL, NULL, NULL, true, 'MED-SAL-100-INH-Q5R6', '789100000048', 'MEDICINE', 1, NOW(), NOW()),
      (49, 'Pantoprazole 40mg', 'Acid reflux relief', 'Eurofarma', 21.30, 42.60, NULL, NULL, NULL, true, 'MED-PAN-40-TAB-S7T8', '789100000049', 'MEDICINE', 1, NOW(), NOW()),
      (50, 'Levothyroxine 50mcg', 'Thyroid medicine', 'Merck', 11.34, 22.68, NULL, NULL, NULL, true, 'MED-LEV-50-TAB-U9V0', '789100000050', 'MEDICINE', 1, NOW(), NOW());

-- =========================
-- MEDICINE DETAILS
-- IDs MUST MATCH PRODUCT IDs (31 to 50)
-- =========================

INSERT INTO medicine_details (
    product_id, dosage, presentation,
    unit_measure, brand_name, generic_name,
    anvisa_registration, expiration_control_required,
    therapeutic_class
) VALUES
      (31, '500', 'TABLET', 'MG', 'Novalgina', 'Dipyrone', 'ANVISA-1001', true, 'ANALGESIC'),
      (32, '600', 'TABLET', 'MG', 'Alivium', 'Ibuprofen', 'ANVISA-1002', true, 'ANTI_INFLAMMATORY'),
      (33, '500', 'CAPSULE', 'MG', 'Amoxil', 'Amoxicillin', 'ANVISA-1003', true, 'ANTIBIOTIC'),
      (34, '100', 'SYRUP', 'ML', 'Loratamed', 'Loratadine', 'ANVISA-1004', true, 'ANTIHISTAMINE'),
      (35, '750', 'TABLET', 'MG', 'Tylenol', 'Paracetamol', 'ANVISA-1005', true, 'ANTIPYRETIC'),
      (36, '50', 'CREAM', 'MG', 'Voltaren', 'Diclofenac', 'ANVISA-1006', true, 'ANTI_INFLAMMATORY'),
      (37, '500', 'TABLET', 'MG', 'Zitromax', 'Azithromycin', 'ANVISA-1007', true, 'ANTIBIOTIC'),
      (38, '20', 'DROPS', 'ML', 'Zyrtec', 'Cetirizine', 'ANVISA-1008', true, 'ANTIHISTAMINE'),
      (39, '20', 'CAPSULE', 'MG', 'Losec', 'Omeprazole', 'ANVISA-1009', true, 'ANTIPYRETIC'),
      (40, '20', 'TABLET', 'MG', 'Meticorten', 'Prednisone', 'ANVISA-1010', true, 'ANTI_INFLAMMATORY'),
      (41, '50', 'TABLET', 'MG', 'Cozaar', 'Losartan', 'ANVISA-1011', true, 'ANTIHYPERTENSIVE'),
      (42, '20', 'TABLET', 'MG', 'Zocor', 'Simvastatin', 'ANVISA-1012', true, 'LIPID_LOWERING'),
      (43, '850', 'TABLET', 'MG', 'Glifage', 'Metformin', 'ANVISA-1013', true, 'ANTIDIABETIC'),
      (44, '100', 'TABLET', 'MG', 'AAS', 'Aspirin', 'ANVISA-1014', true, 'ANALGESIC'),
      (45, '10', 'TABLET', 'MG', 'Renitec', 'Enalapril', 'ANVISA-1015', true, 'ANTIHYPERTENSIVE'),
      (46, '2', 'TABLET', 'MG', 'Rivotril', 'Clonazepam', 'ANVISA-1016', true, 'ANXIOLYTIC'),
      (47, '20', 'CAPSULE', 'MG', 'Prozac', 'Fluoxetine', 'ANVISA-1017', true, 'ANTIDEPRESSANT'),
      (48, '100', 'INHALER', 'MCG', 'Aerolin', 'Salbutamol', 'ANVISA-1018', true, 'BRONCHODILATOR'),
      (49, '40', 'TABLET', 'MG', 'Pantozol', 'Pantoprazole', 'ANVISA-1019', true, 'ANTACID'),
      (50, '50', 'TABLET', 'MCG', 'Puran T4', 'Levothyroxine', 'ANVISA-1020', true, 'HORMONE');

ALTER TABLE categories ALTER COLUMN id RESTART WITH 100;
ALTER TABLE products ALTER COLUMN id RESTART WITH 100;