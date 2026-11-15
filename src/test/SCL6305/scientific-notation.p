/* SCL-6305: OpenEdge 13.0 numeric literals in scientific notation */
define variable d as decimal no-undo.

d = 1.5e3.
d = 1E6.
d = 1.23e-4.
d = 6.02214076e+23.
d = 23766e-1.
d = 1e+3.
d = .5e2.
d = -2e3.
d = 2 + 3e2 - 1.
message 1e3 (d * 1e+3).
d = 1.5E-3 * -2E+2.
