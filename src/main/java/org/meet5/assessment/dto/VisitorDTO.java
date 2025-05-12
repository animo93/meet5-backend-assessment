package org.meet5.assessment.dto;

import java.time.LocalDateTime;

public record VisitorDTO(String visitorName, int visitorAge, LocalDateTime visitedAt) {
}
