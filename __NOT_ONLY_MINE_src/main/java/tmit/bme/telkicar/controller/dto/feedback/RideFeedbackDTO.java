package tmit.bme.telkicar.controller.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RideFeedbackDTO {

	private Boolean completed; //TODO rm
	private List<UserFeedbackDTO> userFeedbacks;

}
