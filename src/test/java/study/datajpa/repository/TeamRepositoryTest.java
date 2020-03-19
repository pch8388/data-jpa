package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class TeamRepositoryTest {

    @Autowired TeamRepository teamRepository;

    @Test
    public void testCRUD() {
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Team findTeam1 = teamRepository.findById(team1.getId()).get();
        Team findTeam2 = teamRepository.findById(team2.getId()).get();

        assertThat(findTeam1).isEqualTo(team1);
        assertThat(findTeam2).isEqualTo(team2);

        List<Team> all = teamRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = teamRepository.count();
        assertThat(count).isEqualTo(2);

        teamRepository.delete(team1);
        teamRepository.delete(team2);

        long deletedCount = teamRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}