(define 
  (problem iot) 
  (:domain iot) 
  (:objects person2 output2 person1 device1 device2 device3 room) 
  (:init 
    (SleepSensor device3) 
    (Television device2) 
    (Asleep person2) 
    (Baby person2) 
    (Event output2) 
    (Adult person1) 
    (Sensor device3) 
    (Sensor device1) 
    (Room room) 
    (DoorbellEvent output2) 
    (Person person2) 
    (Person person1) 
    (Device device3) 
    (Device device1) 
    (Device device2) 
    (State person2) 
    (Doorbell device1) 
    (producedBy output2 device1) 
    (inRoom device2 room) 
    (= 
      (total-cost) 0) 
    (= 
      (SleepingBaby device1) 8) 
    (= 
      (SleepingBaby device2) 8) 
    (= 
      (SleepingBaby device3) 8)) 
  (:goal 
    (and 
      (gotNotifiedFor person1 output2))) 
  (:metric minimize 
    (total-cost)))