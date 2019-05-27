(define (domain iot)
  (:requirements :adl :derived-predicates :action-costs) 
  (:predicates 
    (Person ?p) (Adult ?p) (Baby ?p) 
    (SoundAction ?a) (SoundNotification ?a) (VisualAction ?a)
    (Flat ?f) (inFlat ?o ?f) (hasResident ?f ?r) (inRoom ?r ?o)
    (Event ?e) (DoorbellEvent ?e) (notifiedWith ?p ?a)
    (producedBy ?e ?d) (gotNotifiedFor ?p ?e) (canPerform ?d ?a) 
  )
  
  (:functions (total-cost) (sleeping-baby ?d))

  (:action locate-residents 
    :parameters (?person ?flat ?room)
    :precondition (hasResident ?flat ?person)
    :effect (inRoom ?person ?room))

  (:action notify-with-sound
    :parameters (?device ?action ?person ?flat ?event)
    :precondition (and (canPerform ?device ?action)
                    (SoundAction ?action) (Event ?event)
                    (Flat ?flat) (Person ?person) 
                    (inFlat ?person ?flat) (inFlat ?device ?flat))
    :effect (and (gotNotifiedFor ?person ?event) (notifiedWith ?person ?action) 
            (increase (total-cost) (sleeping-baby ?device))))

  (:action notify-with-visual
    :parameters (?device ?action ?person ?room ?event)
    :precondition (and (canPerform ?device ?action) (VisualAction ?action) 
                    (Event ?event) (Person ?person) 
                    (inRoom ?person ?room) (inRoom ?device ?room))
    :effect (and (gotNotifiedFor ?person ?event) (notifiedWith ?person ?action)))

 (:derived 
    (Event ?e) 
    (DoorbellEvent ?e)) 

 (:derived 
    (SoundAction ?e) 
    (SoundNotification ?e)) 

 (:derived 
    (Person ?p) 
    (or (Adult ?p) (Baby ?p)))
)