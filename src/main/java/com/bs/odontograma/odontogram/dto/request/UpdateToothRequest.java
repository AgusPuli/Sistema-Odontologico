package com.bs.odontograma.odontogram.dto.request;

import com.bs.odontograma.odontogram.enums.ToothCondition;
import com.bs.odontograma.odontogram.enums.TreatmentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToothRequest {

    @NotNull(message = "Condition is required")
    private ToothCondition condition;

    private String observation;

    @Valid
    private List<UpdateSurfaceRequest> surfaces;

    /**
     * Optional treatment-plan status for the history entry that will be auto-created
     * by this update. Defaults to PENDING when null.
     */
    private TreatmentStatus treatmentStatus;

    /**
     * Optional note describing this change. Persisted on the history entry.
     */
    private String historyNote;

    /**
     * Optional ID of a {@code Treatment} from the catalog. When present, the history
     * entry is linked to that procedure so the patient's treatment plan / estimate
     * can render the procedure name and price.
     */
    private UUID treatmentId;
}
