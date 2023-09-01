function addCodeToolElements() {
  // console.debug("addCodeToolElements");
  for (const preElement of document.querySelectorAll("#content pre.highlight")) {
    const codeToolsElement = document.createElement("div");
    codeToolsElement.className = "codetools";
    if (addButtons(preElement, codeToolsElement)) {
      preElement.appendChild(codeToolsElement);
    }
  }
  
  function addButtons(preElement, codeToolsElement) {
    // console.debug("addButtons", preElement, codeToolsElement);
    let numberOfButtons = 0;
    if (window.navigator.clipboard) {
      addCopyButton(preElement, codeToolsElement);
      numberOfButtons ++;
    }
    return numberOfButtons > 0;
  }
  
  function addCopyButton(preElement, codeToolsElement) {
    // console.debug("addCopyButton", preElement, codeToolsElement);
    const copyButton = createButton("Copy to clipboard", "copy-button");
    copyButton.addEventListener(
        "click",
        onCopyButtonClick.bind(copyButton, preElement)
    );
    copyButton.addEventListener("mouseleave", clearClicked.bind(copyButton));
    copyButton.addEventListener("blur", clearClicked.bind(copyButton));
    const copiedPopup = document.createElement("span");
    copyButton.appendChild(copiedPopup);
    copiedPopup.className = "copied";
    codeToolsElement.appendChild(copyButton);
  }
  
  function createButton(label, className) {
    const buttonElement = document.createElement("button");
    buttonElement.className = className;
    buttonElement.title = label;
    buttonElement.type = "button";
    const labelElement = document.createElement("span");
    labelElement.appendChild(document.createTextNode(label));
    labelElement.className = "label";
    buttonElement.appendChild(labelElement);
    return buttonElement;
  }
}

function onCopyButtonClick(preElement) {
  const codeElement = preElement.querySelector("code");
  const copy = codeElement.cloneNode(true);
  for (const hideWhenFoldedElement of copy.querySelectorAll(
      ".hide-when-unfolded"
  )) {
    hideWhenFoldedElement.parentNode.removeChild(hideWhenFoldedElement);
  }
  const text = copy.innerText;
  if (text) {
    window.navigator.clipboard
        .writeText(text + "\n")
        .then(markClicked.bind(this));
  }
}

function markClicked() {
  this.classList.add("clicked");
}

function clearClicked() {
  this.classList.remove("clicked");
}
