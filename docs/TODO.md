# TODO

---
* [x] Add OutlinedTextField validation for capping character count in store name, created by, etc.
* [x] Fix scroll animation effect in SearchScreen
* [ ] Refactor code and change state from mutableStateOf to StateFlow, where necessary
  * [x] SearchViewModel
  * [x] StoreViewModel
  * [x] AuditViewModel
  * [x] HistoryViewModel
  * [x] HomeViewModel
  * [x] ReportViewModel
* [ ] Add better documentation/comments to files
  * [x] Add documentation to UpcValidator and  TextInputFieldValidator
---

## Add Features

* [x] Modify Audit screen such that a SKU can be added via dropdown search box, in addition to scanning
* [ ] Add light/dark theme toggle in top app bar
* [ ] Add basic 'Settings' screen and nav in menu for 'About' app and theme toggle (maybe permissions manager)
  * [ ] Continue Settings screen build; add proper light/dark theme functionality to the switch
  * [ ] Add permissions manager