begin_version
3
end_version
begin_metric
1
end_metric
2
begin_variable
var0
-1
2
Atom inroom(person1, room)
NegatedAtom inroom(person1, room)
end_variable
begin_variable
var1
-1
2
Atom gotnotifiedfor(person1, output1)
NegatedAtom gotnotifiedfor(person1, output1)
end_variable
0
begin_state
1
1
end_state
begin_goal
1
1 0
end_goal
3
begin_operator
locate-people person1 room
0
1
0 0 -1 0
1
end_operator
begin_operator
notify-with-sound person1 output1 device1
0
1
0 1 -1 0
8
end_operator
begin_operator
notify-with-visual person1 output1 device2 room
1
0 0
1
0 1 -1 0
7
end_operator
0
