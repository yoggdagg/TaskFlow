import { reactive, computed } from "vue";

export function useFormValidation() {
  const formData = reactive({
    email: "",
    password: "",
    passwordConfirm: "",
    name: "",
  });

  const errors = reactive({
    email: "",
    password: "",
    passwordConfirm: "",
    name: "",
  });

  // 이메일 유효성 검사
  const validateEmail = () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email) {
      errors.email = "이메일을 입력해주세요.";
      return false;
    } else if (!emailRegex.test(formData.email)) {
      errors.email = "올바른 이메일 형식이 아닙니다.";
      return false;
    } else {
      errors.email = "";
      return true;
    }
  };

  // 비밀번호 유효성 검사
  const validatePassword = () => {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!formData.password) {
      errors.password = "비밀번호를 입력해주세요.";
      return false;
    } else if (formData.password.length < 8) {
      errors.password = "비밀번호는 8자 이상이어야 합니다.";
      return false;
    } else if (!passwordRegex.test(formData.password)) {
      errors.password = "영문과 숫자를 포함해야 합니다.";
      return false;
    } else {
      errors.password = "";
      return true;
    }
  };

  // 비밀번호 확인 검사
  const validatePasswordConfirm = () => {
    if (!formData.passwordConfirm) {
      errors.passwordConfirm = "비밀번호 확인을 입력해주세요.";
      return false;
    } else if (formData.password !== formData.passwordConfirm) {
      errors.passwordConfirm = "비밀번호가 일치하지 않습니다.";
      return false;
    } else {
      errors.passwordConfirm = "";
      return true;
    }
  };

  // 이름 유효성 검사
  const validateName = () => {
    if (!formData.name) {
      errors.name = "이름을 입력해주세요.";
      return false;
    } else if (formData.name.length < 2) {
      errors.name = "이름은 2자 이상이어야 합니다.";
      return false;
    } else {
      errors.name = "";
      return true;
    }
  };

  // 전체 유효성 검사
  const validateForm = () => {
    const isEmailValid = validateEmail();
    const isPasswordValid = validatePassword();
    const isPasswordConfirmValid = validatePasswordConfirm();
    const isNameValid = validateName();

    return (
      isEmailValid && isPasswordValid && isPasswordConfirmValid && isNameValid
    );
  };

  // 폼이 유효한지 확인하는 computed
  const isFormValid = computed(() => {
    return (
      !errors.email &&
      !errors.password &&
      !errors.passwordConfirm &&
      !errors.name &&
      formData.email &&
      formData.password &&
      formData.passwordConfirm &&
      formData.name
    );
  });

  // 폼 초기화
  const resetForm = () => {
    formData.email = "";
    formData.password = "";
    formData.passwordConfirm = "";
    formData.name = "";
    errors.email = "";
    errors.password = "";
    errors.passwordConfirm = "";
    errors.name = "";
  };

  return {
    formData,
    errors,
    validateEmail,
    validatePassword,
    validatePasswordConfirm,
    validateName,
    validateForm,
    isFormValid,
    resetForm,
  };
}
