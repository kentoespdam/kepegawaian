-- RBAC: permission granular per Role (ADR-0037)
-- pref_permission: master daftar permission (format "{ENTITY}:{ACTION}", mis. "CUTI:APPROVE")

CREATE TABLE `pref_permission` (
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- pref_role_permission: join table Role <-> Permission (union semantics per user)

CREATE TABLE `pref_role_permission` (
  `role_id` varchar(50) NOT NULL,
  `perm_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`,`perm_name`) USING BTREE,
  CONSTRAINT `fk_rp_rl_rl_id` FOREIGN KEY (`role_id`) REFERENCES `pref_role` (`id`),
  CONSTRAINT `fk_rp_pm_pm_name` FOREIGN KEY (`perm_name`) REFERENCES `pref_permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
