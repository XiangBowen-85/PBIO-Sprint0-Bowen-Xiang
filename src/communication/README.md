# Communication source mapping

The backend communication implementation is located at:

- `Backend/upload.php`
- `Backend/latest.php`
- `Backend/communication/JsonResponse.php`
- `Web/index.php`

These files implement the design in:

- `doc/communication_design.md`

The communication layer invokes business logic and does not
contain SQL queries or environmental measurement validation.
