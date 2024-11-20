package com.ced.dto.criteria;

import java.util.List;

public record SpellSearchCriteria(
        String className,
        List<Integer> level,
        String schoolName
) {
}