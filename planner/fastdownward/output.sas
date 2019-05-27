begin_version
3
end_version
begin_metric
1
end_metric
26
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
Atom carbonmonoxideexposure(danger)
NegatedAtom carbonmonoxideexposure(danger)
end_variable
begin_variable
var2
-1
2
Atom hasdanger(r3, danger)
Atom ventilated(r3)
end_variable
begin_variable
var3
-1
2
Atom computed-drive-cost(r2, r1)
NegatedAtom computed-drive-cost(r2, r1)
end_variable
begin_variable
var4
-1
2
Atom computed-drive-cost(r1, r2)
NegatedAtom computed-drive-cost(r1, r2)
end_variable
begin_variable
var5
-1
2
Atom inregion(transporter1, r1)
NegatedAtom inregion(transporter1, r1)
end_variable
begin_variable
var6
-1
2
Atom computed-drive-cost(r3, r2)
NegatedAtom computed-drive-cost(r3, r2)
end_variable
begin_variable
var7
-1
2
Atom computed-drive-cost(r3, r4)
NegatedAtom computed-drive-cost(r3, r4)
end_variable
begin_variable
var8
-1
2
Atom computed-drive-cost(r4, r5)
NegatedAtom computed-drive-cost(r4, r5)
end_variable
begin_variable
var9
-1
2
Atom computed-drive-cost(r5, r4)
NegatedAtom computed-drive-cost(r5, r4)
end_variable
begin_variable
var10
-1
2
Atom computed-drive-cost(r5, r6)
NegatedAtom computed-drive-cost(r5, r6)
end_variable
begin_variable
var11
-1
2
Atom computed-drive-cost(r6, r5)
NegatedAtom computed-drive-cost(r6, r5)
end_variable
begin_variable
var12
-1
2
Atom inregion(transporter1, r6)
NegatedAtom inregion(transporter1, r6)
end_variable
begin_variable
var13
-1
2
Atom inregion(transporter1, r5)
NegatedAtom inregion(transporter1, r5)
end_variable
begin_variable
var14
-1
2
Atom inregion(transporter1, r2)
NegatedAtom inregion(transporter1, r2)
end_variable
begin_variable
var15
-1
2
Atom computed-drive-cost(r2, r3)
NegatedAtom computed-drive-cost(r2, r3)
end_variable
begin_variable
var16
-1
2
Atom inregion(transporter1, r4)
NegatedAtom inregion(transporter1, r4)
end_variable
begin_variable
var17
-1
2
Atom computed-drive-cost(r4, r3)
NegatedAtom computed-drive-cost(r4, r3)
end_variable
begin_variable
var18
-1
2
Atom inregion(transporter1, r3)
NegatedAtom inregion(transporter1, r3)
end_variable
begin_variable
var19
-1
2
Atom inregion(emp1, r1)
NegatedAtom inregion(emp1, r1)
end_variable
begin_variable
var20
-1
2
Atom inregion(emp1, r2)
NegatedAtom inregion(emp1, r2)
end_variable
begin_variable
var21
-1
2
Atom inregion(emp1, r3)
NegatedAtom inregion(emp1, r3)
end_variable
begin_variable
var22
-1
2
Atom invehicle(emp1, transporter1)
NegatedAtom invehicle(emp1, transporter1)
end_variable
begin_variable
var23
-1
2
Atom inregion(emp1, r4)
NegatedAtom inregion(emp1, r4)
end_variable
begin_variable
var24
-1
2
Atom inregion(emp1, r5)
NegatedAtom inregion(emp1, r5)
end_variable
begin_variable
var25
-1
2
Atom inregion(emp1, r6)
NegatedAtom inregion(emp1, r6)
end_variable
1
begin_mutex_group
2
1 0
2 1
end_mutex_group
begin_state
1
0
0
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
end_state
begin_goal
1
25 0
end_goal
29
begin_operator
drive-vehicle transporter1 r1 r2
1
0 0
5
0 4 0 1
1 22 0 19 -1 1
1 22 0 20 -1 0
0 5 0 1
0 14 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r2 r1
1
0 0
5
0 3 0 1
1 22 0 19 -1 0
1 22 0 20 -1 1
0 5 -1 0
0 14 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r2 r3
1
0 0
5
0 15 0 1
1 22 0 20 -1 1
1 22 0 21 -1 0
0 14 0 1
0 18 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r3 r2
1
0 0
5
0 6 0 1
1 22 0 20 -1 0
1 22 0 21 -1 1
0 14 -1 0
0 18 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r3 r4
1
0 0
5
0 7 0 1
1 22 0 21 -1 1
1 22 0 23 -1 0
0 18 0 1
0 16 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r4 r3
1
0 0
5
0 17 0 1
1 22 0 21 -1 0
1 22 0 23 -1 1
0 18 -1 0
0 16 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r4 r5
1
0 0
5
0 8 0 1
1 22 0 23 -1 1
1 22 0 24 -1 0
0 16 0 1
0 13 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r5 r4
1
0 0
5
0 9 0 1
1 22 0 23 -1 0
1 22 0 24 -1 1
0 16 -1 0
0 13 0 1
0
end_operator
begin_operator
drive-vehicle transporter1 r5 r6
1
0 0
5
0 10 0 1
1 22 0 24 -1 1
1 22 0 25 -1 0
0 13 0 1
0 12 -1 0
0
end_operator
begin_operator
drive-vehicle transporter1 r6 r5
1
0 0
5
0 11 0 1
1 22 0 24 -1 0
1 22 0 25 -1 1
0 13 -1 0
0 12 0 1
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r1 r2
2
5 0
0 0
1
0 4 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r2 r1
2
14 0
0 0
1
0 3 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r2 r3
3
2 1
14 0
0 0
1
0 15 -1 0
1
end_operator
begin_operator
drive-vehicle-cost transporter1 r2 r3
2
14 0
0 0
1
0 15 -1 0
10
end_operator
begin_operator
drive-vehicle-cost transporter1 r3 r2
2
18 0
0 0
1
0 6 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r3 r4
2
18 0
0 0
1
0 7 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r4 r3
3
2 1
16 0
0 0
1
0 17 -1 0
1
end_operator
begin_operator
drive-vehicle-cost transporter1 r4 r3
2
16 0
0 0
1
0 17 -1 0
10
end_operator
begin_operator
drive-vehicle-cost transporter1 r4 r5
2
16 0
0 0
1
0 8 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r5 r4
2
13 0
0 0
1
0 9 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r5 r6
2
13 0
0 0
1
0 10 -1 0
0
end_operator
begin_operator
drive-vehicle-cost transporter1 r6 r5
2
12 0
0 0
1
0 11 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r1
1
5 0
2
0 19 0 1
0 22 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r2
1
14 0
2
0 20 0 1
0 22 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r3
1
18 0
2
0 21 0 1
0 22 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r4
1
16 0
2
0 23 0 1
0 22 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r5
1
13 0
2
0 24 0 1
0 22 -1 0
0
end_operator
begin_operator
load-employee-to-truck emp1 transporter1 r6
1
12 0
2
0 25 0 1
0 22 -1 0
0
end_operator
begin_operator
ventilate-region r3 danger
0
2
0 1 0 1
0 2 0 1
1
end_operator
1
begin_rule
0
0 1 0
end_rule
