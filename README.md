# Merch Tools

---
## Merch Tools is an inventory & shelf audit assistant for field merchandisers and vendors.

---
## Features (TODO)

- Search product/SKU
- Scan UPC/barcode 
  - Auto fills audit item fields
  - Enter shelf/inventory count
  - Add photo
  - Add note
- Generate a PDF report
- Share/send email to store leadership to rectify inventory issues

---

### Finished Implementations

---

- [x] Data repositories, DAOs, entities, relations, domain mappers
- [x] Home Screen (buttons to nav -> Start Audit, -> Open Audit, -> Audit History)
- [x] Audit Screen (implemented Add Item for adding a blank Audit Item to the list; added a add by 
UPC text field - fills in SKU details if match is found)
- [x] SKU list with search functionality; displays SKU details (UPC, name, case pack, brand)
- [ ] Started working on EditAuditItemScreen (user clicks AuditItem card in Audit list, nav -> EditAuditItem)
  - [x] Implemented the screen UI
  - [x] All fields required (pre-filled UPC, SKU details, editable note and count stepper)
  - [x] Need to implement proper add photo
- [x] Configured type converter to display correct started at time and store object in the database
- [x] Fixed theming issue on physical device (dynamicColor = false)
- [x] Scan UPC/barcode 
  - [x] Auto fills audit item fields 
  - [x] Enter shelf/inventory count 
  - [x] Add photo 
  - [x] Add note
- [x] Generate a PDF report
- [x] Share/send email to store leadership to rectify inventory issues

---

### Next

---

- [x] Implement barcodescanner
- [x] Implement proper SKU catalog and ability to add SKU via manual entry or scan
  - [x] Need to implement barcode scanner

# IMPORTANT

- ~~Need to fix add sku use case, currently when adding SKU the skuID is not passed to edit sku screen~~
- ~~When clicking SKU in list, the skuID is passed, but when clicked saved the changes do not reflect~~
  - ~~Made changes to SkuRepository - changed getSkuById from Flow<Sku?> to Sku?~~

### Fixed

---

## Screenshots

---




---
<img src="docs/screenshots/Screenshot_20251212_063505.png" alt="text" height="400"/> <img src="docs/screenshots/Screenshot_20251212_063610.png" alt="text" height="400"/>
<img src="docs/screenshots/Screenshot_20251212_063709.png" alt="text" height="400"/> 

---

<img src="docs/screenshots/Screenshot_20251212_063740.png" alt="text" height="400"/> <img src="docs/screenshots/Screenshot_20251212_063505.png" alt="text" height="400"/> 
<img src="docs/screenshots/Screenshot_20251212_063610.png" alt="text" height="400"/>

---


<img src="docs/screenshots/Screenshot_20251212_063610.png" alt="text" height="400"/> <img src="docs/screenshots/Screenshot_20251212_063505.png" alt="text" height="400"/>
<img src="docs/screenshots/Screenshot_20251212_063942.png" alt="text" height="400"/>

---

## Screen captures

---

![img](docs/screenshots/gifs/merchtools_demo.gif)

