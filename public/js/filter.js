const navigate = (element) => {
  history.pushState({}, "", element.href);

  for (const docSelector of document.querySelectorAll(".docSelector")) {
    const style = docSelector.dataset.cohort !== element.dataset.cohort ? "display:none" : "";
    docSelector.style = style;
  }

  for (const docView of document.querySelectorAll(".docView")) {
    const style = docView.dataset.cohort !== element.dataset.cohort ? "display:none" : "";
    docView.style = style;
  }

}

export const init = () => {
  for (const cohortSelector of document.querySelectorAll(".cohortSelector")) {
    cohortSelector.addEventListener("click", function (event) {
      event.preventDefault()
      navigate(cohortSelector);
    });
  }
}

init();
