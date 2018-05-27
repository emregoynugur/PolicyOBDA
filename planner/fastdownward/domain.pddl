(define 
  (domain iot) 
  (:requirements :adl :derived-predicates :action-costs) 
  (:predicates 
    (Awake ?a) 
    (SleepSensor ?s) 
    (Television ?t) 
    (Asleep ?a) 
    (SoundAction ?s) 
    (Baby ?b) 
    (SoundNotification ?s) 
    (Event ?e) 
    (VisualNotification ?v) 
    (Adult ?a) 
    (Sensor ?s) 
    (Room ?r) 
    (Action ?a) 
    (DoorbellEvent ?d) 
    (Person ?p) 
    (NotificationType ?n) 
    (Speaker ?s) 
    (Device ?d) 
    (State ?s) 
    (VisualAction ?v) 
    (Doorbell ?d) 
    (producedBy ?s ?o) 
    (gotNotifiedFor ?s ?o) 
    (inRoom ?s ?o)) 
  (:functions 
    (total-cost) 
    (SleepingBaby ?device) 
    (SleepingBaby ?device) 
    (SleepingBaby ?device)) 
  (:action locate-people :parameters 
    (?person ?room) :precondition 
    (and 
      (Room ?room) 
      (Person ?person)) :effect 
    (and 
      (inRoom ?person ?room) 
      (increase (total-cost) 1))) 
  (:action notify-with-sound :parameters 
    (?person ?event ?device) :precondition 
    (and 
      (Person ?person) 
      (Doorbell ?device)) :effect 
    (and 
      (gotNotifiedFor ?person ?event) 
      (increase (total-cost) (SleepingBaby ?device)))) 
  (:action notify-with-visual :parameters 
    (?person ?event ?device ?room) :precondition 
    (and 
      (Person ?person) 
      (Television ?device) 
      (inRoom ?device ?room) 
      (inRoom ?person ?room)) :effect 
    (and 
      (gotNotifiedFor ?person ?event) 
      (increase (total-cost) 1))) 
  (:derived 
    (Event ?e) 
    (DoorbellEvent ?e)) 
  (:derived 
    (Sensor ?s) 
    (or 
      (SleepSensor ?s) 
      (Doorbell ?s))) 
  (:derived 
    (Action ?a) 
    (or 
      (SoundAction ?a) 
      (VisualAction ?a))) 
  (:derived 
    (Person ?p) 
    (or 
      (Adult ?p) 
      (Baby ?p))) 
  (:derived 
    (NotificationType ?n) 
    (or 
      (VisualNotification ?n) 
      (SoundNotification ?n))) 
  (:derived 
    (Device ?d) 
    (or 
      (Television ?d) 
      (Speaker ?d) 
      (Sensor ?d))) 
  (:derived 
    (State ?s) 
    (or 
      (Awake ?s) 
      (Asleep ?s))))