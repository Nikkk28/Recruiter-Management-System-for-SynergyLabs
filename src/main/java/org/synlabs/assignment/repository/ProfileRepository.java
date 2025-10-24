package org.synlabs.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.synlabs.assignment.model.Profile;
import org.synlabs.assignment.model.User;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByApplicant(User applicant);
    Optional<Profile> findByApplicantId(Long applicantId);
}