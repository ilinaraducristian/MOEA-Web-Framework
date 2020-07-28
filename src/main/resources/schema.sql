CREATE TABLE users
(
    id        INT IDENTITY,
    username  VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255)
);

CREATE TABLE problem_solvers
(
    id        INT IDENTITY,
    name  VARCHAR(255) NOT NULL,
    numberOfEvaluations  INT NOT NULL,
    numberOfSeeds     INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    rabbitId VARCHAR(255) NOT NULL,
    results VARCHAR(255) NOT NULL,
    problem VARCHAR(255) NOT NULL,
    algorithm  VARCHAR(255) NOT NULL

);
--    @Id
--     @GeneratedValue
--     var id: Long = 0,
--
--     @Column(nullable = false)
--     @NotBlank
--     @ProblemSolverNameConstraint
--     var name: String = "",
--
--     @Column(nullable = false)
--     var numberOfEvaluations: Int = 10000,
--
--     @Column(nullable = false)
--     var numberOfSeeds: Int = 10,
--
--     @Column(nullable = false)
--     @NotBlank
--     var status: String = "waiting",
--
--     @Column(nullable = false)
--     @NotBlank
--     var rabbitId: String = "",
--
--     @Column(nullable = false, columnDefinition = "MEDIUMBLOB")
--     var results: ArrayList<QualityIndicators> = ArrayList(),
--
--     @Column(nullable = false)
--     var problem: String = "",
--
--     @Column(nullable = false)
--     var algorithm: String = "",
--
--     @ManyToOne
--     @JoinColumn(name = "user_id")
--     @JsonIgnore
--     var user: User = User()