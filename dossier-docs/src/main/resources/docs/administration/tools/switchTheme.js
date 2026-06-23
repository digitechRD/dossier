function switchInitialTheme() {
  if (isInitialThemeDark()) {
    document.documentElement.classList.add("dark-theme");
  }
}

function initTheme() {
  const toggleCheckboxElement = document.querySelector(
      "#switch-theme-checkbox"
  );
  toggleCheckboxElement.checked = isInitialThemeDark();
  toggleCheckboxElement.addEventListener("change", onThemeChange.bind(toggleCheckboxElement));
}

function isInitialThemeDark() {
  const theme = loadTheme();
  return theme ? theme === "dark" : window.matchMedia("(prefers-color-scheme: dark)").matches;
}

function onThemeChange() {
  if (this.checked) {
    document.documentElement.classList.add("dark-theme");
    saveTheme("dark");
  } else {
    document.documentElement.classList.remove("dark-theme");
    saveTheme("light");
  }
  try {
    handleTocOnResize();
  } catch (err) {
  }
}

function saveTheme(theme) {
  const localStorage = window.localStorage;
  if (localStorage) {
    localStorage.setItem("theme", theme);
  }
}

function loadTheme() {
  const localStorage = window.localStorage;
  return localStorage !== null ? localStorage.getItem("theme") : null;
}

