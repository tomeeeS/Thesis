package tmit.bme.telkicar.controller.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserFeedbackDTO {

	private Integer userId;
	private Integer rating;
	private Boolean wasPresent;
	private String description;
}
