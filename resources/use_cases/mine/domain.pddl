(define (domain smart-mine)
  (:requirements :adl :derived-predicates :action-costs) 
  (:predicates 
    (Region ?r) (Dangerous ?r) (Employee ?e) (inVehicle ?e ?v)
    (Transporter ?t) (Vehicle ?v) (hasDanger ?r ?d)
    (inRegion ?s ?o) (connected ?s ?o) (CarbonMonoxideExposure ?d)
  )
  
  (:functions (total-cost) (HighCarbonMonoxide ?r))

  ;ideally this action should decrease the cost of the prohibition policy: (decrease (HighCarbonMonoxide ?region) 1)
  ;however, not all planners support "decrease" functionality
  (:action ventilate-region
    :parameters (?region ?danger)
    :precondition (and (Region ?region) (hasDanger ?region ?danger) (CarbonMonoxideExposure ?danger))
    :effect(and (not (hasDanger ?region ?danger)) (not (CarbonMonoxideExposure ?danger)) (increase (total-cost) 1)))

  ; assuming there is always a driver inside a truck
  (:action drive-vehicle
    :parameters (?truck ?from ?to)
    :precondition (and (Vehicle ?truck) (Region ?from) (Region ?to) (inRegion ?truck ?from) (connected ?from ?to))
    :effect(and (not (inRegion ?truck ?from)) (inRegion ?truck ?to) (increase (total-cost) (HighCarbonMonoxide ?to))
            (forall (?per ?truck) 
                    (when (inVehicle ?per ?truck)
                          (and (not (inRegion ?per ?from)) (inRegion ?per ?to))))))

  (:action load-employee-to-truck
    :parameters (?per ?truck ?loc)
    :precondition (and (Employee ?per) (Transporter ?truck) (Region ?loc)
                     (inRegion ?truck ?loc) (inRegion ?per ?loc))
    :effect (and (not (inRegion ?per ?loc)) (inVehicle ?per ?truck)))
 
 (:derived 
    (Vehicle ?v) 
    (Transporter ?v)) 
)