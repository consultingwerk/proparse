/* ON CREATE OF table */
ON CREATE OF Customer DO:
END.

/* ON DELETE OF table */
ON DELETE OF Customer DO:
END.

/* ON FIND OF table */
ON FIND OF Customer DO:
END.

/* ON WRITE OF table */
ON WRITE OF Customer DO:
END.

/* ON WRITE OF table with NEW and OLD buffers */
ON WRITE OF Customer NEW new-cust OLD old-cust DO:
END.

/* ON WRITE OF table with NEW BUFFER and OLD BUFFER */
ON WRITE OF Customer NEW BUFFER new-cust OLD BUFFER old-cust DO:
END.

/* ON ASSIGN OF field */
ON ASSIGN OF Customer.CustNum DO:
END.

/* ON ASSIGN OF field with OLD VALUE */
ON ASSIGN OF Customer.CustNum OLD VALUE old-val DO:
END.

/* ON DELETE OF table with OVERRIDE */
ON DELETE OF Customer OVERRIDE DO:
END.

/* ON DELETE OF table - REVERT */
ON DELETE OF Customer REVERT.
