package br.com.climbORM.framework;

import br.com.climbORM.framework.mapping.ID;

public abstract class PersistentEntity {

	@ID
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;
		}
		
		if (this == obj) {
			return true;
		}
		
		PersistentEntity p = (PersistentEntity)obj;
		
		if (p.getId().equals(getId())) {
			return true;
		}
		
		return false;
	}
	
}
