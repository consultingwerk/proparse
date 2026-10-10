/*------------------------------------------------------------------------
    File        : SmartSchedulerJobPlanQuery.cls
    Purpose     : SCL-6029 fixture - the shape the Business Entity Generator
                  emits for the enum predicates of a TableQuery class on
                  OpenEdge 13.0 and above: a bound generic type with two
                  type arguments on a single line
    Notes       : Reduced from UnitTests/Consultingwerk/BusinessEntityGeneratorTest/
                  SCL5388/SchedulerJobPlanBusinessEntity/SmartSchedulerJobPlanQuery.cls
  ----------------------------------------------------------------------*/

USING Consultingwerk.OERA.QueryBuilder.* FROM PROPATH.
USING Consultingwerk.SmartFramework.Scheduler.* FROM PROPATH.

CLASS SCL6029.SmartSchedulerJobPlanQuery INHERITS TableQuery:

    DEFINE PUBLIC PROPERTY PlanningType AS GenericCharacterEnumPredicate<SmartSchedulerJobPlanQuery, Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum> NO-UNDO
    GET.
    PROTECTED SET.

    DEFINE PUBLIC PROPERTY JobName AS GenericCharacterPredicate<SmartSchedulerJobPlanQuery> NO-UNDO
    GET.
    PROTECTED SET.

    CONSTRUCTOR PUBLIC SmartSchedulerJobPlanQuery ():
        SUPER ("eSmartSchedulerJobPlan":U).

        THIS-OBJECT:PlanningType = NEW GenericCharacterEnumPredicate<SmartSchedulerJobPlanQuery, Consultingwerk.SmartFramework.Scheduler.SchedulerJobPlanningTypeEnum> (THIS-OBJECT, "PlanningType":U).
        THIS-OBJECT:JobName      = NEW GenericCharacterPredicate<SmartSchedulerJobPlanQuery> (THIS-OBJECT, "JobName":U).
    END CONSTRUCTOR.

END CLASS.
