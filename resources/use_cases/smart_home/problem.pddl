(define 
  (problem iot) 
  (:domain iot) 
  (:objects output1 device1 device2 room minute second notification1 kelvin notification2 person2 person1 hour celsius flat) 
  (:init 
    (Device device1) 
    (Device device2) 
    (TimeUnit minute) 
    (TimeUnit hour) 
    (TimeUnit second) 
    (Flat flat) 
    (TemperatureUnit celsius) 
    (TemperatureUnit kelvin) 
    (Person person2) 
    (Person person1) 
    (Event output1) 
    (Unit celsius) 
    (Unit minute) 
    (Unit hour) 
    (Unit kelvin) 
    (Unit second) 
    (OutputData output1) 
    (NotificationType notification1) 
    (NotificationType notification2) 
    (Room room) 
    (IndoorSpace room) 
    (IndoorSpace flat) 
    (Baby person2) 
    (DoorbellOutput output1) 
    (SoundNotification notification1) 
    (Sensor device1) 
    (VisualNotification notification2) 
    (Doorbell device1) 
    (Television device2) 
    (Space room) 
    (Space flat) 
    (Adult person1) 
    (belongsTo device2 flat) 
    (belongsTo device1 flat) 
    (enabledNotificationType person1 notification2) 
    (residesIn person1 flat) 
    (residesIn person2 flat) 
    (producedBy output1 device1) 
    (disabledNotificationType person1 notification1) 
    (inRoom device2 room) 
    (= 
      (total-cost) 0) 
    (= 
      (SoundDisabled device2) 8) 
    (= 
      (SoundDisabled device1) 8)) 
  (:goal 
    (and 
      (gotNotifiedFor person1 output1))) 
  (:metric minimize 
    (total-cost)))