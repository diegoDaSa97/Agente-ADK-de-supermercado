// Autor: Diego Alexander Sánchez
package com.udea.agente_mcp;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.StreamableHttpServerParameters;

import java.time.Duration;
import java.util.List;

public class TareaAgent {
    public static BaseAgent ROOT_AGENT= buildAgent();

    private static BaseAgent buildAgent() {
        StreamableHttpServerParameters params = StreamableHttpServerParameters.builder()
                .url("http://localhost:8088/mcp")
                .timeout(Duration.ofSeconds(30L))
                .build();
        McpToolset mcpToolset = new McpToolset(params);

        try{
            List<BaseTool> mcpTools = mcpToolset.getTools(null)
                    .toList()
                    .blockingGet();
            System.out.println("Tools MCP: " + mcpTools.size());

            return LlmAgent.builder()
                    .name("SupermercadoAgent")
                    .model("gemini-1.5-flash")
                    .description("Agente de pedidos de supermercado a domicilio")
                    .instruction("Eres un asistente de un supermercado con entrega a domicilio. Usa las herramientas MCP para mostrar productos, crear pedidos, agregar productos, consultar, confirmar y cancelar pedidos. Antes de crear un pedido pide el nombre del cliente y la dirección de entrega. Muestra el total al final. Antes de cancelar un pedido, confirma el id con el usuario. Responde siempre en español.")
                    .tools(mcpTools)
                    .build();

        } catch (Exception e) {
            System.err.println("MCP no disponible en http://localhost:8088/mcp: " + e.getMessage());
            return LlmAgent.builder()
                    .name("SupermercadoAgent")
                    .model("gemini-1.5-flash")
                    .description("Agente de pedidos no disponible")
                    .instruction("El servidor MCP no está disponible")
                    .build();
        }
    }
}