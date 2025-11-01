var character global-login.

on write of Customer new n-Customer old o-Customer do:
	run C:\Work\Proparse\GitHub\proparse\src\test\SCL4868\testProc.p (buffer o-Customer, buffer n-Customer, global-login).
end.