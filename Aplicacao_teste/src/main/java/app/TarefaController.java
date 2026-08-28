package app;

import flash.annotations.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Remote
public class TarefaController {

    private final Map<Integer, Tarefa> tarefas = new HashMap<>();
    private AtomicInteger contadorId = new AtomicInteger(1);

    @MethodMapping(method = MethodHTTP.POST, path = "/tarefas")
    public Tarefa criar(JSONObject body) {
        int id = contadorId.getAndIncrement();
        String titulo = body.getString("titulo");
        String descricao = body.optString("descricao", "");

        Tarefa tarefa = new Tarefa(id, titulo, descricao);
        tarefas.put(id, tarefa);
        return tarefa;
    }

    @MethodMapping(method = MethodHTTP.GET, path = "/tarefas")
    public List<Tarefa> listar() {
        return new ArrayList<>(tarefas.values());
    }

    @MethodMapping(method = MethodHTTP.GET, path = "/tarefas/{id}")
    public Tarefa obter(@Param(name = "id") int id) {
        Tarefa tarefa = tarefas.get(id);
        if (tarefa == null) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
        return tarefa;
    }

    @MethodMapping(method = MethodHTTP.PUT, path = "/tarefas/{id}")
    public Tarefa atualizar(@Param(name = "id") int id, JSONObject body) {
        Tarefa tarefa = tarefas.get(id);
        if (tarefa == null) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }

        if (body.has("titulo")) tarefa.setTitulo(body.getString("titulo"));
        if (body.has("descricao")) tarefa.setDescricao(body.getString("descricao"));
        if (body.has("status")) tarefa.setStatus(body.getString("status"));

        return tarefa;
    }

    @MethodMapping(method = MethodHTTP.DELETE, path = "/tarefas/{id}")
    public String remover(@Param(name = "id") int id) {
        Tarefa removed = tarefas.remove(id);
        if (removed == null) {
            throw new IllegalArgumentException("Tarefa não encontrada");
        }
        return "Tarefa removida com sucesso";
    }
}


