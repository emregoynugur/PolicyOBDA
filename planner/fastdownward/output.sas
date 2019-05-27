begin_version
3
end_version
begin_metric
1
end_metric
4
begin_variable
var0
0
2
Atom person(bob)
NegatedAtom person(bob)
end_variable
begin_variable
var1
-1
2
Atom inroom(bob, room)
NegatedAtom inroom(bob, room)
end_variable
begin_variable
var2
0
2
Atom event(e1)
NegatedAtom event(e1)
end_variable
begin_variable
var3
-1
2
Atom gotnotifiedfor(bob, e1)
NegatedAtom gotnotifiedfor(bob, e1)
end_variable
0
begin_state
1
1
1
1
end_state
begin_goal
1
3 0
end_goal
2
begin_operator
locate-residents bob flt room
0
1
0 1 -1 0
0
end_operator
begin_operator
notify-with-visual television displaymessage bob room e1
3
2 0
1 0
0 0
1
0 3 -1 0
0
end_operator
2
begin_rule
0
2 1 0
end_rule
begin_rule
0
0 1 0
end_rule
