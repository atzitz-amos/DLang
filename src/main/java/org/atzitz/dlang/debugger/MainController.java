package org.atzitz.dlang.debugger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.atzitz.dlang.compile.bytecode.ByteCode;
import org.atzitz.dlang.compile.bytecode.bytecodes.*;
import org.atzitz.dlang.compile.parser.Parser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final Debugger debugger;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/debugger")
    public String debugger() {
        return "debugger";
    }

    @PostMapping(value = "/debugger/api/load")
    public ResponseEntity<String> load(@RequestBody() JsonNode raw) {
        debugger.load(raw.get("raw").asText());
        return ResponseEntity.ok("OK");
    }

    @GetMapping(value = "/debugger/api/bytecodify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AbstractBytecode>> bytecodify(@RequestParam("raw") String raw) {
        Parser parser = new Parser(raw);
        parser.parse();
        return ResponseEntity.ok(ByteCode.of(parser, raw).getBytecodes());
    }


    @PostMapping(value = "/debugger/api/bytecodify/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> queryBytecode(@RequestParam(value = "pc", defaultValue = "-1") Integer pc, @RequestBody(required = false) JsonNode raw) {
        ObjectMapper mapper = new ObjectMapper();

        if (pc != -1) {
            JsonNode node = mapper.valueToTree(debugger.bytecodeAt(pc));
            ((ObjectNode) node).put("repr", BCfromJson(node).toString());
            return ResponseEntity.ok(node);
        }

        Parser parser = new Parser(raw.get("raw").asText());
        parser.parse();

        JsonNode node = mapper.valueToTree(ByteCode.of(parser, raw.get("raw").asText()).getBytecodes());
        for (JsonNode n : node) {
            ((ObjectNode) n).put("repr", BCfromJson(n).toString());
        }
        return ResponseEntity.ok(node);
    }

    @GetMapping(value = "/debugger/api/bytecodify/format")
    public ResponseEntity<String> formatBytecode(@RequestBody() JsonNode bc) {
        return ResponseEntity.ok((BCfromJson(bc)).toString());
    }

    private AbstractBytecode BCfromJson(JsonNode bc) {
        return switch (bc.get("type").asInt()) {
            case AbstractBytecode.Type.LoadThis -> new BCLoadThis(bc.get("offset").asInt());
            case AbstractBytecode.Type.BinOp -> new BCBinOp(bc.get("op").asText(), bc.get("offset").asInt());
            case AbstractBytecode.Type.BuildCls ->
                    new BCBuildCls(bc.get("id").asInt(), bc.get("argc").asInt(), bc.get("localc").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.Compare -> new BCCompare(bc.get("op").asText(), bc.get("offset").asInt());
            case AbstractBytecode.Type.ExchangeAssign -> {
                int[] ids = new int[bc.get("size").asInt()];
                int i = 0;
                for (JsonNode node : bc.get("ids")) {
                    ids[i] = node.asInt();
                    i++;
                }
                yield new BCExchangeAssign(ids, bc.get("size").asInt(), bc.get("offset").asInt());
            }
            case AbstractBytecode.Type.FuncAlloc ->
                    new BCFuncAlloc(bc.get("until").asInt(), bc.get("pointer").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.FuncInit -> new BCInitFunc(bc.get("localc").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.InvokeFunc ->
                    new BCInvokeFunc(bc.get("obj").asInt(), bc.get("id").asInt(), bc.get("argc")
                            .asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.Jump -> new BCJump(bc.get("jump").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.JumpIfFalse ->
                    new BCJumpIfFalse(bc.get("jump").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.LoadRel ->
                    new BCLoadRel(bc.get("obj").asInt(), bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.LoadConst -> new BCLoadConst(bc.get("name").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.LoadDynamic -> new BCLoadDynamic(bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.LoadGlobal -> new BCLoadGlobal(bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.LoadParam -> new BCLoadParam(bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.StoreRel -> new BCStoreRel(bc.get("obj").asInt(), bc.get("id").asInt(),
                    bc.get("offset").asInt());
            case AbstractBytecode.Type.StoreDynamic ->
                    new BCStoreDynamic(bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.StoreGlobal -> new BCStoreGlobal(bc.get("id").asInt(), bc.get("offset").asInt());
            case AbstractBytecode.Type.Return -> new BCReturn(bc.get("offset").asInt());
            default -> throw new IllegalStateException(STR."Unexpected value: \{bc.get("type").asInt()}");
        };
    }


    @PostMapping(value = "/debugger/api/memory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> fetchMemory() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("SP", debugger.$SP());
        root.put("LOCAL", debugger.$LOCAL());
        root.put("ARG", debugger.$ARG());
        root.put("THIS", debugger.$THIS());
        root.put("PC", debugger.getPC());

        ArrayNode memory = root.putArray("memory");
        for (int i = 0; i < debugger.getMaxSP(); i++) {
            memory.add(debugger.getMemoryAt(i));
        }

        return ResponseEntity.ok(root);
    }

    @PostMapping(value = "/debugger/api/heap", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> fetchHeap() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("THIS", debugger.$THIS());

        ArrayNode memory = root.putArray("heap");
        for (int i = 0; i < debugger.getMaxSP(); i++) {
            memory.add(debugger.getHeapAt(i));
        }

        return ResponseEntity.ok(root);
    }

    @PostMapping(value = "/debugger/api/step")
    public ResponseEntity<String> step() {
        debugger.step();
        return ResponseEntity.ok("OK");
    }
}
