let RAW_DATA = null;
let BYTECODE_DATA = null;

let memoryBlocks = [];
let heapBlocks = [];

window.onload = async () => {
    const urlParams = window.location.search.slice(1);

    const index = urlParams.indexOf("raw=");
    if (index === -1) {
        window.location.replace("/");
    }
    RAW_DATA = decodeURI(urlParams.slice(index + 4));

    await fetch("/debugger/api/load", {
        headers: {
            'Accept': 'application/json', 'Content-Type': 'application/json'
        }, body: JSON.stringify({raw: RAW_DATA}), method: "POST"
    });

    BYTECODE_DATA = await fetch("/debugger/api/bytecodify/query", {
        headers: {
            'Accept': 'application/json', 'Content-Type': 'application/json'
        }, body: JSON.stringify({raw: RAW_DATA}), method: "POST"
    }).then(async response => await response.json());
    loadRaw();
    await loadMemory();
    await loadHeap();

};

function loadRaw() {
    RAW_DATA.split('\n').forEach((line, index) => {
        const lineDiv = document.createElement("div");
        const lineNumberDiv = document.createElement("div");
        const lineContent = document.createElement("div");

        lineDiv.className = "code-line";
        lineNumberDiv.innerText = index + 1;

        lineNumberDiv.className = "line-number";
        lineContent.textContent = line;

        lineContent.className = "line-content";

        lineContent.style.textIndent = (line.search(/\S/) * 4) + "px";
        lineDiv.appendChild(lineNumberDiv);
        lineDiv.appendChild(lineContent);
        document.querySelector(".code-content").appendChild(lineDiv);

    });

}

function loadBytecode() {
    if (BYTECODE_DATA == null) {
        console.log("Waiting for bytecode data to load")
        return setTimeout(loadBytecode, 1000);

    }
    BYTECODE_DATA.forEach((line, index) => {
        console.log(line);

        const lineDiv = document.createElement("div");
        const lineNumberDiv = document.createElement("div");
        const lineContent = document.createElement("div");
        lineDiv.className = "code-line";

        lineNumberDiv.innerText = index + 1;
        lineNumberDiv.className = "line-number";

        lineContent.textContent = line.repr;

        lineContent.className = "line-content";

        lineDiv.appendChild(lineNumberDiv);
        lineDiv.appendChild(lineContent);
        document.querySelector(".code-content").appendChild(lineDiv);
    });

}

function changed() {
    document.querySelector(".code-content").innerHTML = "";
    if (document.querySelector(".toggle-checkbox").checked) {
        loadRaw();
    } else {
        loadBytecode();
    }


}

/* ---------------------------------------------- */
function resize_to_fit(container) {
    let fontSize = parseInt(window.getComputedStyle(container, null).getPropertyValue('font-size'));
    console.log(fontSize, container.scrollWidth, container.offsetWidth, container.clientWidth, container.scrollWidth > 20);
    while (container.scrollWidth > 24 && fontSize > 0) {
        fontSize--;
        container.style.fontSize = fontSize + 'px';
    }
}


async function requireMemory() {
    return await fetch("/debugger/api/memory", {method: "post"}).then(async response => await response.json());
}

async function requireHeap() {
    return await fetch("/debugger/api/heap", {method: "post"}).then(async response => await response.json());
}

function createMemoryBlock(mem, i, maxWidth) {
    let block = document.createElement("div");
    block.className = "memory-block";
    block.id = "memory-block-" + i;

    document.querySelector(".right").appendChild(block);

    let memory_content = document.createElement("div");
    memory_content.className = "memory-content";
    block.appendChild(memory_content);

    for (let i = 0; i < maxWidth; i++) {
        let memory_cell = document.createElement("div");
        memory_cell.className = "memory-cell";
        memory_cell.innerText = mem[i] || 0;
        memory_content.appendChild(memory_cell);
        resize_to_fit(memory_cell);
    }

    let memory_bottom = document.createElement("div");
    memory_bottom.className = "memory-bottom";
    block.appendChild(memory_bottom);

    let memory_bottom_sp = document.createElement("span");
    memory_bottom_sp.className = "sp-indic";
    memory_bottom_sp.innerText = "SP";
    memory_bottom.appendChild(memory_bottom_sp);

    let memory_bottom_local = document.createElement("span");
    memory_bottom_local.className = "loc-indic";
    memory_bottom_local.innerText = "LOC";
    memory_bottom.appendChild(memory_bottom_local);

    let memory_bottom_param = document.createElement("span");
    memory_bottom_param.className = "param-indic";
    memory_bottom_param.innerText = "PAR";
    memory_bottom.appendChild(memory_bottom_param);

    let memory_bottom_this = document.createElement("span");
    memory_bottom_this.className = "this-indic";
    memory_bottom_this.innerText = "THS";
    memory_bottom.appendChild(memory_bottom_this);


    return block;
}

function setIndic(name, value, maxCells) {
    if (value === -1) return;
    let offset = Math.floor(value / maxCells);
    let cell = value % maxCells;

    document.querySelector("#memory-block-" + offset + " .memory-bottom").style.setProperty(name, cell);
}

async function setNextInstr(PC) {
    await fetch("/debugger/api/bytecodify/query?pc=" + PC, {"method": "post"}).then(
        response => {
            response.json().then(
                bytecode => {
                    document.querySelector(".code-line.active")?.classList.remove("active");
                    document.querySelector(`.code-line:nth-child(${PC + 1})`)?.classList.add("active");
                    document.querySelector(".next-instruction").innerText = bytecode.repr;
                }
            )
        }
    )
}

async function loadMemory() {
    document.querySelector(".right").innerHTML = "";
    memoryBlocks = [];

    const availableWidth = document.querySelector(".right").getBoundingClientRect().width - 100;
    const cellWidth = parseInt(window.getComputedStyle(document.querySelector(".right")).getPropertyValue("--memory-cell-width").slice(0, -2));
    const availableCells = Math.ceil(availableWidth / cellWidth);

    const memoryJSON = await requireMemory();
    const memory = memoryJSON.memory;

    let len = memory.length;
    let i = 0;
    if (len === 0) {
        memoryBlocks.push(createMemoryBlock([], 0, availableCells));
    }
    while (len > 0) {
        len -= availableCells;
        memoryBlocks.push(createMemoryBlock(memory.slice(i * availableCells, i * availableCells + availableCells), i, availableCells));

        i++;
    }

    setIndic("--sp-pos", memoryJSON.SP, availableCells);
    setIndic("--loc-pos", memoryJSON.LOCAL, availableCells);
    setIndic("--param-pos", memoryJSON.ARG, availableCells);

    await setNextInstr(memoryJSON.PC);
}

async function loadHeap() {
    memoryBlocks = [];

    const availableWidth = document.querySelector(".right").getBoundingClientRect().width - 100;
    const cellWidth = parseInt(window.getComputedStyle(document.querySelector(".right")).getPropertyValue("--memory-cell-width").slice(0, -2));
    const availableCells = Math.ceil(availableWidth / cellWidth);

    const heapJSON = await requireHeap();
    const heap = heapJSON.heap;

    let len = heap.length;
    let i = 0;
    if (len === 0) {
        heapBlocks.push(createMemoryBlock([], 1, availableCells));
    }
    while (len > 0) {
        len -= availableCells;
        heapBlocks.push(createMemoryBlock(heap.slice(i * availableCells, i * availableCells + availableCells), i+1, availableCells));

        i++;
    }

    setIndic("--this-pos", heapJSON.THIS + availableCells, availableCells);
}

/* ---------------------------------------- */

function fw() {
    fetch("/debugger/api/step", {method: "post"}).then(async response => {
        await loadMemory();
        await loadHeap();
    });
}