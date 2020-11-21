INSERT INTO user_entity -- user with one algorithm/problem/reference_set
VALUES ('cdd36e48-f1c5-474e-abc3-ac7a17909878');

INSERT INTO user_entity -- user with no algorithm/problem/reference_set
VALUES ('cdd36e48-f1c5-474e-abc3-ac7a17909879');

INSERT INTO user_entity -- user with multiple algorithm/problem/reference_set
VALUES ('cdd36e48-f1c5-474e-abc3-ac7a17909880');

-- add 2 algorithms
INSERT INTO algorithms
VALUES (0, 'NSGAIII', 'ce63b31c3fb6519d1c71f0b5de2979fb');

INSERT INTO algorithms
VALUES (1, 'CustomNSGAIII', '7d4f9a76003a1de375ca5481506400c1');

-- add 2 problems
INSERT INTO problems
VALUES (0, 'Belegundu', '45b19eb42c0f9288927d5f7f3e4ecb82');

INSERT INTO problems
VALUES (1, 'CustomBelegundu', '0c4b8ba81503587a2b4afed32ab5cdb3');

-- add 2 reference sets
INSERT INTO reference_sets
VALUES (0, 'Belegundu', 'c835346a50b0d2335483bb0d6e439df0');

INSERT INTO reference_sets
VALUES (1, 'CustomBelegundu', '24b769e488eacc77fa561ed62f380a41');

-- add a queue item to the first user
INSERT INTO queue_items
VALUES(0, 'New Queue', 15000, 10, 'waiting',
'8896b0d0-dbf5-40db-b982-b2c4a7918368',
'',
'ce63b31c3fb6519d1c71f0b5de2979fb',
'45b19eb42c0f9288927d5f7f3e4ecb82',
'c835346a50b0d2335483bb0d6e439df0',
'cdd36e48-f1c5-474e-abc3-ac7a17909878');

-- add the first algorithm/problem/reference_set to the first user
INSERT INTO algorithm_user_entity
VALUES(0, 'cdd36e48-f1c5-474e-abc3-ac7a17909878', 0);

INSERT INTO problem_user_entity
VALUES(0, 'cdd36e48-f1c5-474e-abc3-ac7a17909878', 0);

INSERT INTO reference_set_user_entity
VALUES(0, 'cdd36e48-f1c5-474e-abc3-ac7a17909878', 0);

-- add the first algorithm/problem/reference_set to the third user
INSERT INTO algorithm_user_entity
VALUES(1, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 0);

INSERT INTO problem_user_entity
VALUES(1, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 0);

INSERT INTO reference_set_user_entity
VALUES(1, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 0);

-- add the second algorithm/problem/reference_set to the third user
INSERT INTO algorithm_user_entity
VALUES(2, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 1);

INSERT INTO problem_user_entity
VALUES(2, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 1);

INSERT INTO reference_set_user_entity
VALUES(2, 'cdd36e48-f1c5-474e-abc3-ac7a17909880', 1);