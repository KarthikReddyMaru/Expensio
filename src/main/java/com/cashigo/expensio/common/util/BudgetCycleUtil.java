package com.cashigo.expensio.common.util;

import com.cashigo.expensio.dto.mapper.UUIDMapper;
import com.cashigo.expensio.dto.projection.BudgetDefCacheProjection;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class BudgetCycleUtil {

    private static final ThreadLocal<Map<Long, TreeMap<Instant, BudgetDefCacheProjection>>> budgetCycleCache = new ThreadLocal<>();

    public static void createCache(List<BudgetDefCacheProjection> budgetDefinitionsCache) {
        Map<Long, TreeMap<Instant, BudgetDefCacheProjection>> categoryCacheMap = budgetDefinitionsCache
                .stream()
                .collect(
                        Collectors.groupingBy(BudgetDefCacheProjection::getCategoryId,
                                Collectors.toMap(
                                        BudgetDefCacheProjection::getStart,
                                        Function.identity(),
                                        (e, r) -> e,
                                        TreeMap::new
                                )
                        ));
        budgetCycleCache.set(categoryCacheMap);
    }

    public static UUID getBudgetCycleByCategory(long categoryId, Instant cycleStart) {

        cycleStart = cycleStart.truncatedTo(ChronoUnit.SECONDS);

        TreeMap<Instant, BudgetDefCacheProjection> cache = budgetCycleCache.get().get(categoryId);
        if (cache != null) {
            BudgetDefCacheProjection value = cache.floorEntry(cycleStart).getValue();
            if (value != null && (
                    value.getEnd().truncatedTo(ChronoUnit.SECONDS).isAfter(cycleStart) ||
                    value.getEnd().truncatedTo(ChronoUnit.SECONDS).equals(cycleStart)
            ))
                return new UUIDMapper().mapToUUID(value.getBudgetCycleId());
        }
        return null;
    }

    public static void clearCache() {
        budgetCycleCache.remove();
    }

}
