import {
    create,
    search,
    insert,
} from "https://cdn.jsdelivr.net/npm/@orama/orama@3.1.10/+esm";

import { Highlight } from 'https://cdn.jsdelivr.net/npm/@orama/highlight@0.1.9/+esm'

const db = create({
    schema: {
        name: "string",
        description: "string",
        link: "string",
        extraTerms: "string[]",
    },
});


window.addEventListener("load", () => {
    let assetsUrl = document.getElementById("search-script").dataset.assetspath
    let pageUrl = document.getElementById("search-script").dataset.path


    fetch(assetsUrl + '/searchEntries.json')
        .then(response => {
            return response.json();
        }).then(data => {
            for (const entry of data) {
                insert(db, {
                    name: entry.name,
                    description: entry.description,
                    link: entry.link,
                    extraTerms: entry.extraTerms,
                });
            }
        })

    let searchButton = document.getElementById("search-button")
    searchButton.addEventListener("click", () => {
        if (document.getElementById("search-overlay") !== null) {
            document.getElementById("search-overlay").remove()
            document.getElementById("overlay-background").remove()
            return
        }

        if (window.innerWidth <= 800) {
            document.getElementById("sidebar").dataset.toggled = "true"
        }


        let searchOverlay = document.createElement("div")
        searchOverlay.id = "search-overlay"
        document.getElementById("page").appendChild(searchOverlay)

        let overlayBackground = document.createElement("div")
        overlayBackground.id = "overlay-background"
        document.body.appendChild(overlayBackground)

        overlayBackground.addEventListener("click", () => {
            searchOverlay.remove()
            overlayBackground.remove()
        })

        let searchBox = document.createElement("input")
        searchBox.id = "search"
        searchOverlay.appendChild(searchBox)
        searchBox.focus()

        let resultContainer = document.createElement("div")
        resultContainer.id = "search-results"
        searchOverlay.appendChild(resultContainer)

        searchBox.addEventListener("input", () => {
            const searchResults = search(db, {
                term: searchBox.value,
                boost: {
                  title: 2,
                },
            });

            resultContainer.innerHTML = ''

            for (const entry of searchResults.hits) {
                const highlighter = new Highlight()
                const highlighted = highlighter.highlight(
                    entry.document.name + "\n" + entry.document.description,
                    searchBox.value
                )

                let entryDiv = document.createElement("div")

                let name = document.createElement("a")
                name.textContent = entry.document.name
                name.href = relativePath(pageUrl, entry.document.link)
                name.addEventListener('click', (event) => {
                    searchOverlay.remove()
                    overlayBackground.remove()
                });
                entryDiv.appendChild(name)

                let description = document.createElement("p")
                description.innerHTML = highlighted.trim(100)
                entryDiv.appendChild(description)

                resultContainer.appendChild(entryDiv)
            }
        })
    })


    let themeSelect = document.getElementById("theme-select")
    themeSelect.addEventListener("change", () => {

        fetch(assetsUrl + '/themes.json')
            .then(response => {
                return response.json();
            }).then(data => {
                let themeCSS = [];

                for (const [key, value] of Object.entries(data[themeSelect.value])) {
                    document.documentElement.style.setProperty(`--${key}`, `#${value}`);
                    themeCSS.push([key, value])
                }
                localStorage.setItem("theme", themeSelect.value);
                localStorage.setItem("themeCSS", JSON.stringify(themeCSS));
            })
    })

    function relativePath(from, to) {
        try {
            new URL(to);
            return to;
        } catch {
        }

        const fromParts = from.split("/").slice(0, -1); // drop file
        const toParts = to.split("/");

        let i = 0;
        while (i < fromParts.length && i < toParts.length && fromParts[i] === toParts[i]) {
            i++;
        }

        const upMoves = fromParts.length - i;
        const downMoves = toParts.slice(i);

        return "../".repeat(upMoves) + downMoves.join("/");
    }

    const currentTheme = localStorage.getItem("theme");
    if (currentTheme != null) {
        themeSelect.value = currentTheme
    }

  const sidebar = document.getElementById('sidebar');
  const current = document.getElementById('current-page');

  sidebar.scrollTop = current.offsetTop - sidebar.offsetTop - (sidebar.clientHeight / 2) + (current.clientHeight / 2);
})

