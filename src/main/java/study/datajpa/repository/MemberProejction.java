package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface MemberProejction {

    Long getId();
    String getUsername();
    String getTeamName();
}
