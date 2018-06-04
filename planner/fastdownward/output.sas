begin_version
3
end_version
begin_metric
1
end_metric
16
begin_variable
var0
0
2
Atom vehicle(transporter1)
NegatedAtom vehicle(transporter1)
end_variable
begin_variable
var1
-1
2
Atom inregion(transporter1, r1)
NegatedAtom inregion(transporter1, r1)
end_variable
begin_variable
var2
-1
2
Atom inregion(transporter1, r6)
NegatedAtom inregion(transporter1, r6)
end_variable
begin_variable
var3
-1
2
Atom inregion(transporter1, r5)
NegatedAtom inregion(transporter1, r5)
end_variable
begin_variable
var4
-1
2
Atom inregion(transporter1, r3)
NegatedAtom inregion(transporter1, r3)
end_variable
begin_variable
var5
-1
2
Atom inregion(transporter1, r2)
NegatedAtom inregion(transporter1, r2)
end_variable
begin_variable
var6
-1
2
Atom inregion(transporter1, r4)
NegatedAtom inregion(transporter1, r4)
end_variable
begin_variable
var7
-1
2
Atom inregion(transporter1, r7)
NegatedAtom inregion(transporter1, r7)
end_variable
begin_variable
var8
-1
2
Atom inregion(emp1, r1)
NegatedAtom inregion(emp1, r1)
end_variable
begin_variable
var9
-1
2
Atom inregion(emp1, r3)
NegatedAtom inregion(emp1, r3)
end_variable
begin_variable
var10
-1
2
Atom inregion(emp1, r5)
NegatedAtom inregion(emp1, r5)
end_variable
begin_variable
var11
-1
2
Atom inregion(emp1, r7)
NegatedAtom inregion(emp1, r7)
end_variable
begin_variable
var12
-1
2
Atom invehicle(emp1, transporter1)
NegatedAtom invehicle(emp1, transporter1)
end_variable
begin_variable
var13
-1
2
Atom inregion(emp1, r2)
NegatedAtom inregion(emp1, r2)
end_variable
begin_variable
var14
-1
2
Atom inregion(emp1, r4)
NegatedAtom inregion(emp1, r4)
end_variable
begin_variable
var15
-1
2
Atom inregion(emp1, r6)
NegatedAtom inregion(emp1, r6)
end_variable
0
begin_state
1
0
1
1
1
1
1
1
0
1
1
1
1
1
1
1
end_state
begin_goal
1
15 0
end_goal
21
begin_operator
drive-vehicle transporter1 r1 r2
1
0 0
4
1 12 0 8 -1 1
1 12 0 13 -1 0
0 1 0 1
0 5 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r2 r1
1
0 0
4
1 12 0 8 -1 0
1 12 0 13 -1 1
0 1 -1 0
0 5 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r2 r3
1
0 0
4
1 12 0 13 -1 1
1 12 0 9 -1 0
0 5 0 1
0 4 -1 0
10
end_operator
begin_operator
drive-vehicle transporter1 r2 r7
1
0 0
4
1 12 0 13 -1 1
1 12 0 11 -1 0
0 5 0 1
0 7 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r3 r2
1
0 0
4
1 12 0 13 -1 0
1 12 0 9 -1 1
0 5 -1 0
0 4 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r3 r4
1
0 0
4
1 12 0 9 -1 1
1 12 0 14 -1 0
0 4 0 1
0 6 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r4 r3
1
0 0
4
1 12 0 9 -1 0
1 12 0 14 -1 1
0 4 -1 0
0 6 0 1
10
end_operator
begin_operator
drive-vehicle transporter1 r4 r5
1
0 0
4
1 12 0 14 -1 1
1 12 0 10 -1 0
0 6 0 1
0 3 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r4 r7
1
0 0
4
1 12 0 14 -1 1
1 12 0 11 -1 0
0 6 0 1
0 7 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r5 r4
1
0 0
4
1 12 0 14 -1 0
1 12 0 10 -1 1
0 6 -1 0
0 3 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r5 r6
1
0 0
4
1 12 0 10 -1 1
1 12 0 15 -1 0
0 3 0 1
0 2 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r6 r5
1
0 0
4
1 12 0 10 -1 0
1 12 0 15 -1 1
0 3 -1 0
0 2 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r7 r2
1
0 0
4
1 12 0 13 -1 0
1 12 0 11 -1 1
0 5 -1 0
0 7 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r7 r4
1
0 0
4
1 12 0 14 -1 0
1 12 0 11 -1 1
0 6 -1 0
0 7 0 1
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r1
1
1 0
2
0 8 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r2
1
5 0
2
0 13 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r3
1
4 0
2
0 9 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r4
1
6 0
2
0 14 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r5
1
3 0
2
0 10 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r6
1
2 0
2
0 15 0 1
0 12 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r7
1
7 0
2
0 11 0 1
0 12 -1 0
0
end_operator
1
begin_rule
0
0 1 0
end_rule
