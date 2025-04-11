package id.perumdamts.kepegawaian.services.setupMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SetupPrefRole implements SetupMaster{
    private final PrefRoleRepository repository;
    @Override
    public void insertBatch() throws JsonProcessingException {
        List<PrefRole> list=new ArrayList<>();
        list.add(new PrefRole("SYSTEM"));
        list.add(new PrefRole("ADMIN"));
        list.add(new PrefRole("USER"));
        list.add(new PrefRole("HRD"));
        list.add(new PrefRole("PENGGAJIAN"));

        repository.saveAll(list);
    }
}
