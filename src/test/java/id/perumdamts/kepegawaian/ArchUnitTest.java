package id.perumdamts.kepegawaian;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "id.perumdamts.kepegawaian")
public class ArchUnitTest {

    @ArchTest
    public static final ArchRule controllersPagedRequestMustBeValid = methods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
            .and().arePublic()
            .should(haveValidOnPagedRequestParams())
            .allowEmptyShould(true);

    private static ArchCondition<JavaMethod> haveValidOnPagedRequestParams() {
        return new ArchCondition<>("have @Valid on all PagedRequest parameters") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (JavaParameter parameter : method.getParameters()) {
                    JavaClass type = parameter.getRawType();
                    if (type.isAssignableTo(PagedRequest.class)) {
                        boolean hasValid = parameter.isAnnotatedWith(Valid.class);
                        if (!hasValid) {
                            String message = String.format(
                                    "Method %s.%s parameter %s is missing @Valid annotation",
                                    method.getOwner().getName(),
                                    method.getName(),
                                    type.getName()
                            );
                            events.add(SimpleConditionEvent.violated(method, message));
                        }
                    }
                }
            }
        };
    }
}
