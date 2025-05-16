package es.codeurjc.web.nitflex.configuration;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;

@ControllerAdvice(basePackages = "es.codeurjc.web.nitflex.controller.web")
public class WebErrorHandler {

	public static final String MESSAGE = "message";

    /**
	 * When a 'FilmNotFound' exception occurs, the following method is executed
	 * @param ex
	 * @return a view with a message indicating the error
	 */
	@ExceptionHandler({FilmNotFoundException.class, IllegalArgumentException.class, BindException.class})
    public ModelAndView handleException(Exception ex){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(MESSAGE);
		modelAndView.addObject("error", true);

		if(ex instanceof MethodArgumentNotValidException manvExp){
			FieldError fieldError = manvExp.getFieldError();
			if(fieldError != null) {
				modelAndView.addObject(MESSAGE, fieldError.getDefaultMessage());
			} else {
				modelAndView.addObject(MESSAGE, "Invalid input provided");
			}
		}else{
			modelAndView.addObject(MESSAGE, ex.getMessage());
		}

        return modelAndView;
    }

}
