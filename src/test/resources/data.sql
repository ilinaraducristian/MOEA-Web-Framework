INSERT INTO users
VALUES (0, 'moeawebframework', 'moeawebframework', 'moeawebframework@bar.com', 'moeawebframework', 'moeawebframework');

INSERT INTO users
VALUES (1, 'foobar', 'foobar', 'foo@bar.com', 'Foo', 'Bar');

INSERT INTO algorithms
VALUES (0, 'CustomNSGAIII', 'CustomNSGAIII.class');

INSERT INTO problems
VALUES (0, 'Belegundu', 'Belegundu.class');

INSERT INTO reference_sets
VALUES (0, 'Belegundu', 'Belegundu.pf');

INSERT INTO processes
VALUES (0, 'New Process', 10000, 10, 'waiting',
        '0abdf521-8500-47da-9321-2c164e078349', '',
        'CustomNSGAIII.class', 'Belegundu.class', 'Belegundu.pf', 1);