(define 
  (domain iot) 
  (:requirements :adl :derived-predicates :action-costs) 
  (:predicates 
    (TemperatureOutput ?t) 
    (Action ?a) 
    (Hall ?h) 
    (Device ?d) 
    (TimeUnit ?t) 
    (Apartment ?a) 
    (Flat ?f) 
    (Effects ?e) 
    (Speaker ?s) 
    (Input ?i) 
    (TemperatureUnit ?t) 
    (Door ?d) 
    (Person ?p) 
    (Event ?e) 
    (Unit ?u) 
    (OutputData ?o) 
    (NotificationType ?n) 
    (Room ?r) 
    (Parameter ?p) 
    (EmailNotification ?e) 
    (Preconditions ?p) 
    (VisualAction ?v) 
    (Location ?l) 
    (IndoorSpace ?i) 
    (Baby ?b) 
    (SoundAction ?s) 
    (DoorbellOutput ?d) 
    (HumidityUnit ?h) 
    (Platform ?p) 
    (SoundNotification ?s) 
    (Sensor ?s) 
    (VisualNotification ?v) 
    (Doorbell ?d) 
    (TemperatureSensor ?t) 
    (Television ?t) 
    (Space ?s) 
    (SMSNotification ?s) 
    (Adult ?a) 
    (Building ?b) 
    (HumidityOutput ?h) 
    (hasOutputData ?s ?o) 
    (belongsTo ?s ?o) 
    (enabledNotificationType ?s ?o) 
    (getNotifiedWith ?s ?o) 
    (residesIn ?s ?o) 
    (hasUnit ?s ?o) 
    (gotNotifiedFor ?s ?o) 
    (hasHumidityUnit ?s ?o) 
    (producedBy ?s ?o) 
    (disabledNotificationType ?s ?o) 
    (hasTemperatureUnit ?s ?o) 
    (produces ?s ?o) 
    (hasNotificationType ?s ?o) 
    (inRoom ?s ?o) 
    (hasLocation ?s ?o) 
    (canNotifyWith ?s ?o) 
    (hasOutputType ?s ?o)) 
  (:functions 
    (total-cost) 
    (SoundDisabled ?device)) 
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
      (increase (total-cost) (SoundDisabled ?device)))) 
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
    (Action ?a) 
    (or 
      (SoundAction ?a) 
      (VisualAction ?a))) 
  (:derived 
    (Device ?d) 
    (or 
      (Television ?d) 
      (Speaker ?d) 
      (Sensor ?d))) 
  (:derived 
    (Person ?p) 
    (or 
      (Adult ?p) 
      (Baby ?p))) 
  (:derived 
    (Unit ?u) 
    (or 
      (HumidityUnit ?u) 
      (TemperatureUnit ?u) 
      (TimeUnit ?u))) 
  (:derived 
    (OutputData ?o) 
    (or 
      (HumidityOutput ?o) 
      (DoorbellOutput ?o) 
      (TemperatureOutput ?o))) 
  (:derived 
    (NotificationType ?n) 
    (or 
      (SMSNotification ?n) 
      (EmailNotification ?n) 
      (VisualNotification ?n) 
      (SoundNotification ?n))) 
  (:derived 
    (IndoorSpace ?i) 
    (or 
      (Hall ?i) 
      (Flat ?i) 
      (Room ?i) 
      (Building ?i) 
      (Apartment ?i))) 
  (:derived 
    (Platform ?p) 
    (Door ?p)) 
  (:derived 
    (Sensor ?s) 
    (or 
      (TemperatureSensor ?s) 
      (Doorbell ?s))) 
  (:derived 
    (Space ?s) 
    (IndoorSpace ?s)) 
  (:derived 
    (hasUnit ?s ?o) 
    (or 
      (hasHumidityUnit ?s ?o) 
      (hasTemperatureUnit ?s ?o))))