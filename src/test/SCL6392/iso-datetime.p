/* SCL-6392: ISO 8601 DATETIME / DATETIME-TZ constants */
define variable dt  as datetime    no-undo initial 2026-11-24T10:30:00.123.
define variable dtz as datetime-tz no-undo initial 2026-11-24T10:30:00.123+05:00.
define variable dta as datetime    no-undo extent 2 initial [2026-11-24T10:30:00, 2026-11-25T11:00].

define temp-table ttIso no-undo
    field DtField  as datetime    initial 2026-11-24T10:30:00.123
    field DtzField as datetime-tz initial 2026-11-24t10:30:00-05:00
    field DaField  as date        initial 11/24/2026 .

dt = 2026-11-24T10:30:00.
dtz = 2026-11-24T10:30:00.123+0500 + 1000.
if dt = 2026-11-24T10:30 then
    message 2026-11-24T10:30:00.123456.
