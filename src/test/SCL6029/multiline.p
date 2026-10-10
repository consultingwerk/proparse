/* SCL-6029 fixture - the reproduction of the ticket: the type name, the
   opening bracket and the second type argument each start on a new line. */

USING Consultingwerk.OERA.QueryBuilder.*.
USING Consultingwerk.SmartFramework.Scheduler.*.

DEFINE VARIABLE o AS

GenericCharacterEnumPredicate
<SmartSchedulerJobPlanQuery,
Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum>

NO-UNDO.

DEFINE VARIABLE oSpaced AS Pair< Alpha , Beta > NO-UNDO.
