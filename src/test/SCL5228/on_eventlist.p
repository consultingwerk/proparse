/* ON event-list ANYWHERE */
ON CLOSE OF THIS-PROCEDURE DO:
END.

/* ON event-list OF widget-list (system handle) */
ON DELETE OF THIS-PROCEDURE DO:
END.

/* ON event-list OF widget-list (procedure handle) */
ON U1 OF THIS-PROCEDURE DO:
END.

/* ON event-list ANYWHERE - developer event */
ON U1 ANYWHERE DO:
END.

/* ON multiple events OF widget */
ON CLOSE, U1 OF THIS-PROCEDURE DO:
END.

/* ON event OF widget OR event OF widget */
ON CLOSE OF THIS-PROCEDURE OR U1 OF THIS-PROCEDURE DO:
END.

/* ON event OF widget ANYWHERE */
ON CLOSE OF THIS-PROCEDURE ANYWHERE DO:
END.

/* ON event-list - REVERT */
ON CLOSE OF THIS-PROCEDURE REVERT.

/* ON event-list - PERSISTENT RUN */
ON CLOSE OF THIS-PROCEDURE PERSISTENT RUN myproc.p.

/* ON event-list - PERSISTENT RUN with parameters */
ON CLOSE OF THIS-PROCEDURE PERSISTENT RUN myproc.p ("param1").
