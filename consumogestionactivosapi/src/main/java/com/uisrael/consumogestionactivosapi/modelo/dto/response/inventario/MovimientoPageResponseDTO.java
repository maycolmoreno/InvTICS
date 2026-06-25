package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

import java.util.List;

public class MovimientoPageResponseDTO {

    private List<MovimientoInventarioResponseDTO> content;
    private int number;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;

    public List<MovimientoInventarioResponseDTO> getContent() { return content; }
    public void setContent(List<MovimientoInventarioResponseDTO> content) { this.content = content; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }
    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
