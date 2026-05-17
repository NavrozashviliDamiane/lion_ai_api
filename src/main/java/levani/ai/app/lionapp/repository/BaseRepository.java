package levani.ai.app.lionapp.repository;

import levani.ai.app.lionapp.domain.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BaseRepository extends JpaRepository<Base, Integer> {

    List<Base> findByAuthor(Integer author);

    List<Base> findByAuthorOrderByDateDesc(Integer author);

    @Query(value = "SELECT * FROM vps_liontest_db.base WHERE author = :author ORDER BY date DESC", nativeQuery = true)
    List<Base> findByAuthorNative(@Param("author") Integer author);

    List<Base> findByManufacturer(Integer manufacturer);

    List<Base> findByModel(Integer model);

    List<Base> findByYear(String year);

    List<Base> findByVinContaining(String vin);

    List<Base> findByBuyerId(String buyerId);

    List<Base> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Base> findByAuthorAndDateBetween(Integer author, LocalDate startDate, LocalDate endDate);
}
