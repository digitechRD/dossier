// window.addEventListener("load", onWindowLoad);

function buildTabs() {
  // console.debug("buildTabs");
  addTabs();
  configureTabs();
}

function addTabs() {
  for (const primaryElement of document.querySelectorAll(".primary")) {
    // console.debug("addTabs primaryElement", primaryElement);
    if (primaryElement.querySelector("div.switch")) {
      console.debug("Skipping tabs due to existing blockswitches");
      return;
    }
    const tabsElement = createTabsElement(primaryElement);
    const tab = createTab(primaryElement, tabsElement);
    tab.tabElement.classList.add("selected");
    primaryElement.querySelector(".title").remove();
    primaryElement.classList.add("tabs-content");
  }
  for (const secondaryElement of document.querySelectorAll(".secondary")) {
    const primaryElement = findPrimaryElement(secondaryElement);
    // console.debug("addTabs primaryElement secondaryElement", primaryElement, secondaryElement);
    if (primaryElement) {
      const tabsElement = primaryElement.querySelector(".tabs");
      const tab = createTab(secondaryElement, tabsElement);
      tab.content.classList.add("hidden");
      primaryElement.append(tab.content);
      secondaryElement.remove();
    } else {
      console.error("Found secondary block with no primary sibling");
    }
  }
}

function createTabsElement(primaryElement) {
  const tabsElement = createElementFromHtml('<div class="tabs"></div>');
  primaryElement.prepend(tabsElement);
  return tabsElement;
}

function createTab(blockElement, tabsElement) {
  const title = blockElement.querySelector(".title").textContent;
  const content = blockElement.querySelectorAll(".content").item(0);
  const colist = nextSibling(blockElement, ".colist", ".listingblock");
  
  // console.debug("createTab", title, content, colist);
  if (colist) {
    content.append(colist);
  }
  const tabElement = createElementFromHtml(
      '<div class="tab">' + title + "</div>"
  );
  tabElement.dataset.blockName = title;
  content.dataset.blockName = title;
  tabsElement.append(tabElement);
  return {tabElement: tabElement, content: content};
}

function nextSibling(element, selector, stopSelector) {
  let sibling = element.nextElementSibling;
  while (sibling) {
    if (sibling.matches(selector)) {
      return sibling;
    }
    if (sibling.matches(stopSelector)) {
      return;
    }
    sibling = sibling.nextElementSibling;
  }
}

function createElementFromHtml(html) {
  const template = document.createElement("template");
  template.innerHTML = html;
  return template.content.firstChild;
}

function findPrimaryElement(secondaryElement) {
  let candidate = secondaryElement.previousElementSibling;
  while (candidate && !candidate.classList.contains("primary")) {
    candidate = candidate.previousElementSibling;
  }
  return candidate;
}

function configureTabs() {
  for (const tabElement of document.querySelectorAll(".tab")) {
    const tabId = getTabId(tabElement);
    // console.debug("configureTabs", tabId);
    tabElement.addEventListener("click", onTabClick.bind(tabElement, tabId));
    if (tabElement.textContent === window.localStorage.getItem(tabId)) {
      select(tabElement);
    }
  }
}

function onTabClick(tabId) {
  const title = this.textContent;
  window.localStorage.setItem(tabId, title);
  for (const tabElement of document.querySelectorAll(".tab")) {
    if (getTabId(tabElement) === tabId && tabElement.textContent === title) {
      select(tabElement);
    }
  }
}

function select(tabElement) {
  for (const child of tabElement.parentNode.children) {
    child.classList.remove("selected");
  }
  tabElement.classList.add("selected");
  for (const child of tabElement.parentNode.parentNode.children) {
    if (child.classList.contains("content")) {
      if (tabElement.dataset.blockName === child.dataset.blockName) {
        child.classList.remove("hidden");
      } else {
        child.classList.add("hidden");
      }
    }
  }
}

function getTabId(tabElement) {
  const id = [];
  for (tabElement of tabElement.parentNode.querySelectorAll(".tab")) {
    id.push(tabElement.textContent.toLowerCase());
  }
  return id.sort().join("-");
}