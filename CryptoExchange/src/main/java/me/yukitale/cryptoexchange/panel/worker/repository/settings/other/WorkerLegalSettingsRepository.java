package me.yukitale.cryptoexchange.panel.worker.repository.settings.other;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.yukitale.cryptoexchange.panel.worker.model.settings.other.WorkerLegalSettings;

import java.util.Optional;

@Repository
public interface WorkerLegalSettingsRepository extends JpaRepository<WorkerLegalSettings, Long> {

    Optional<WorkerLegalSettings> findByWorkerId(long workerId);

    void deleteByWorkerId(long  workerId);
}
